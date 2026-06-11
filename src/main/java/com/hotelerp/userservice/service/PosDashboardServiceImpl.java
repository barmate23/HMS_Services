package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.posdashboard.*;
import com.hotelerp.userservice.entity.*;
import com.hotelerp.userservice.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PosDashboardServiceImpl implements PosDashboardService {

        private final PosOrderRepository posOrderRepository;
        private final PosBillRepository posBillRepository;
        private final DiningTableRepository diningTableRepository;
        private final PosOrderItemRepository posOrderItemRepository;

        @Override
        public StandardResponse<PosOpsDashboardDTO> getPosDashboardData() {
                try {
                        // 1. Floor Pulse
                        List<DiningTable> allTables = diningTableRepository.findAll();
                        int totalTables = allTables.size();
                        int occupied = (int) allTables.stream()
                                        .filter(t -> t.getStatus() != null
                                                        && "OCCUPIED".equalsIgnoreCase(t.getStatus().getValue()))
                                        .count();
                        int available = (int) allTables.stream()
                                        .filter(t -> t.getStatus() != null
                                                        && "AVAILABLE".equalsIgnoreCase(t.getStatus().getValue()))
                                        .count();
                        int reserved = (int) allTables.stream()
                                        .filter(t -> t.getStatus() != null
                                                        && "RESERVED".equalsIgnoreCase(t.getStatus().getValue()))
                                        .count();

                        FloorPulseDTO floorPulse = FloorPulseDTO.builder()
                                        .totalTables(totalTables)
                                        .occupied(occupied)
                                        .available(available)
                                        .reserved(reserved)
                                        .occupiedPercent(totalTables > 0 ? (double) occupied / totalTables * 100 : 0)
                                        .availablePercent(totalTables > 0 ? (double) available / totalTables * 100 : 0)
                                        .reservedPercent(totalTables > 0 ? (double) reserved / totalTables * 100 : 0)
                                        .build();

                        // 2. KOT Queue (Active Orders)
                        List<PosOrder> activeOrders = posOrderRepository.findAll().stream()
                                        .filter(o -> o.getStatus() != null && ("OPEN"
                                                        .equalsIgnoreCase(o.getStatus().getValue()) ||
                                                        "KOT_SENT".equalsIgnoreCase(o.getStatus().getValue()) ||
                                                        "HELD".equalsIgnoreCase(o.getStatus().getValue())))
                                        .sorted(Comparator.comparing(PosOrder::getCreatedAt).reversed())
                                        .limit(10)
                                        .collect(Collectors.toList());

                        List<KotQueueDTO> kotQueue = activeOrders.stream().map(o -> KotQueueDTO.builder()
                                        .orderId("ORD-" + o.getId())
                                        .outletName(o.getOutlet() != null ? o.getOutlet().getName() : "Unknown Outlet")
                                        .info((o.getDiningTable() != null ? o.getDiningTable().getTableNumber()
                                                        : "TAKEAWAY") + " • "
                                                        + (o.getGuestName() != null ? o.getGuestName() : "Guest"))
                                        .itemCount(o.getItems().size())
                                        .status(o.getStatus() != null ? o.getStatus().getValue() : "N/A")
                                        .build()).collect(Collectors.toList());

                        // 3. Revenue Mix & 4. Payment Split
                        List<PosBill> settledBills = posBillRepository.findAll().stream()
                                        .filter(b -> b.getStatus() != null
                                                        && "SETTLED".equalsIgnoreCase(b.getStatus().getValue()))
                                        .collect(Collectors.toList());

                        Map<String, List<PosBill>> revenuePerOutlet = settledBills.stream()
                                        .collect(Collectors.groupingBy(b -> b.getOrder().getOutlet().getName()));

                        List<OutletRevenueDTO> revenueMix = revenuePerOutlet.entrySet().stream()
                                        .map(entry -> OutletRevenueDTO.builder()
                                                        .outletName(entry.getKey())
                                                        .billCount(entry.getValue().size())
                                                        .totalAmount(entry.getValue().stream().map(PosBill::getAmount)
                                                                        .filter(Objects::nonNull)
                                                                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                                                        .build())
                                        .collect(Collectors.toList());

                        BigDecimal totalRevenue = settledBills.stream().map(PosBill::getAmount).filter(Objects::nonNull)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                        Map<String, List<PosBill>> paymentsByMethod = settledBills.stream()
                                        .filter(b -> b.getPaymentMethod() != null)
                                        .collect(Collectors.groupingBy(b -> b.getPaymentMethod().getValue()));

                        List<PaymentSplitDTO> paymentSplit = paymentsByMethod.entrySet().stream().map(entry -> {
                                BigDecimal amount = entry.getValue().stream().map(PosBill::getAmount)
                                                .filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
                                double percent = totalRevenue.compareTo(BigDecimal.ZERO) > 0
                                                ? amount.divide(totalRevenue, 4, RoundingMode.HALF_UP)
                                                                .multiply(BigDecimal.valueOf(100)).doubleValue()
                                                : 0;
                                return PaymentSplitDTO.builder()
                                                .method(entry.getKey())
                                                .amount(amount)
                                                .percentage(percent)
                                                .build();
                        }).collect(Collectors.toList());

                        // 5. Fast Moving Items
                        Map<MenuItem, Integer> itemSales = posOrderItemRepository.findAll().stream()
                                        .collect(Collectors.groupingBy(PosOrderItem::getMenuItem,
                                                        Collectors.summingInt(PosOrderItem::getQuantity)));

                        List<FastMovingItemDTO> fastMovingItems = itemSales.entrySet().stream()
                                        .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                                        .limit(5)
                                        .map(entry -> FastMovingItemDTO.builder()
                                                        .itemName(entry.getKey().getItemName())
                                                        .outletName(entry.getKey().getOutlet() != null
                                                                        ? entry.getKey().getOutlet().getName()
                                                                        : "N/A")
                                                        .soldQty(entry.getValue())
                                                        .imageUrl(null) // Mocked as it's byte array in DB
                                                        .build())
                                        .collect(Collectors.toList());

                        // 6. Billing Watch
                        List<PosBill> allBills = posBillRepository.findAll();
                        int openBillsCount = (int) allBills.stream()
                                        .filter(b -> b.getStatus() != null
                                                        && "PENDING".equalsIgnoreCase(b.getStatus().getValue()))
                                        .count();
                        BigDecimal openBillsAmount = allBills.stream()
                                        .filter(b -> b.getStatus() != null
                                                        && "PENDING".equalsIgnoreCase(b.getStatus().getValue()))
                                        .map(PosBill::getAmount).filter(Objects::nonNull)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                        int roomPostingPending = (int) settledBills.stream().filter(b -> b.getPaymentMethod() != null
                                        && "ROOM_CHARGE".equalsIgnoreCase(b.getPaymentMethod().getValue())).count();
                        BigDecimal roomPostingAmount = settledBills.stream()
                                        .filter(b -> b.getPaymentMethod() != null && "ROOM_CHARGE"
                                                        .equalsIgnoreCase(b.getPaymentMethod().getValue()))
                                        .map(PosBill::getAmount).filter(Objects::nonNull)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                        int voidsCount = (int) allBills.stream().filter(
                                        b -> b.getStatus() != null && "VOID".equalsIgnoreCase(b.getStatus().getValue()))
                                        .count();
                        BigDecimal voidsAmount = allBills.stream()
                                        .filter(b -> b.getStatus() != null
                                                        && "VOID".equalsIgnoreCase(b.getStatus().getValue()))
                                        .map(PosBill::getAmount).filter(Objects::nonNull)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                        BillingWatchDTO billingWatch = BillingWatchDTO.builder()
                                        .openBillsCount(openBillsCount)
                                        .openBillsAmount(openBillsAmount)
                                        .roomPostingPendingCount(roomPostingPending)
                                        .roomPostingPendingAmount(roomPostingAmount)
                                        .voidsCount(voidsCount)
                                        .voidsAmount(voidsAmount)
                                        .build();

                        // 7. Recent Activity
                        List<RecentActivityDTO> recentActivity = new ArrayList<>();
                        // Mock some activity based on recent orders/bills
                        activeOrders.stream().limit(3).forEach(o -> recentActivity.add(RecentActivityDTO.builder()
                                        .activityType("KOT sent to kitchen")
                                        .linkedEntityId("ORD-" + o.getId())
                                        .timestamp(o.getCreatedAt())
                                        .build()));
                        settledBills.stream().sorted(Comparator.comparing(PosBill::getCreatedAt).reversed()).limit(2)
                                        .forEach(b -> recentActivity.add(RecentActivityDTO.builder()
                                                        .activityType("Bill settled by " + (b.getPaymentMethod() != null
                                                                        ? b.getPaymentMethod().getValue()
                                                                        : "N/A"))
                                                        .linkedEntityId("BILL-" + b.getId())
                                                        .timestamp(b.getCreatedAt())
                                                        .build()));

                        PosOpsDashboardDTO dashboardDTO = PosOpsDashboardDTO.builder()
                                        .floorPulse(floorPulse)
                                        .kotQueue(kotQueue)
                                        .revenueMix(revenueMix)
                                        .paymentSplit(paymentSplit)
                                        .fastMovingItems(fastMovingItems)
                                        .billingWatch(billingWatch)
                                        .recentActivity(recentActivity)
                                        .build();

                        return StandardResponse.success(dashboardDTO,
                                        "POS Operations Dashboard data fetched successfully");

                } catch (Exception e) {
                        log.error("Error fetching POS dashboard data: ", e);
                        return StandardResponse.error("Failed to fetch POS dashboard data", "INTERNAL_SERVER_ERROR",
                                        e.getMessage());
                }
        }
}
