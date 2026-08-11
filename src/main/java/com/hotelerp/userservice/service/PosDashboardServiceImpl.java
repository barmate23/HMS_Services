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
        private final OutletRepository outletRepository;

        @Override
        public StandardResponse<PosOpsDashboardDTO> getPosDashboardData() {
                try {
                        // 1. Floor Pulse
                        List<DiningTable> allTables = diningTableRepository.findByIsDeletedFalse();
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
                        List<PosBill> nonVoidBills = posBillRepository.findAll().stream()
                                        .filter(b -> b.getIsDeleted() == null || !b.getIsDeleted())
                                        .filter(b -> b.getStatus() == null
                                                        || !"VOID".equalsIgnoreCase(b.getStatus().getValue()))
                                        .collect(Collectors.toList());

                        Map<String, List<PosBill>> revenuePerOutlet = nonVoidBills.stream()
                                        .filter(b -> b.getOrder() != null && b.getOrder().getOutlet() != null)
                                        .collect(Collectors.groupingBy(b -> b.getOrder().getOutlet().getName()));

                        java.util.function.Function<PosBill, BigDecimal> calculateBillAmount = b -> {
                                if (b.getPaidAmount() != null && b.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
                                        return b.getPaidAmount();
                                }
                                BigDecimal net = b.getNetAmount() != null ? b.getNetAmount()
                                                : (b.getGrossAmount() != null ? b.getGrossAmount() : BigDecimal.ZERO);
                                BigDecimal gst = b.getGstAmount() != null ? b.getGstAmount()
                                                : net.multiply(new BigDecimal("0.18"));
                                return net.add(gst).setScale(2, RoundingMode.HALF_UP);
                        };

                        List<OutletRevenueDTO> revenueMix = revenuePerOutlet.entrySet().stream()
                                        .map(entry -> OutletRevenueDTO.builder()
                                                        .outletName(entry.getKey())
                                                        .billCount(entry.getValue().size())
                                                        .totalAmount(entry.getValue().stream()
                                                                        .map(calculateBillAmount)
                                                                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                                                        .build())
                                        .collect(Collectors.toList());

                        BigDecimal totalRevenue = nonVoidBills.stream()
                                        .map(calculateBillAmount)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                        Map<String, List<PosBill>> paymentsByMethod = nonVoidBills.stream()
                                        .collect(Collectors.groupingBy(b -> (b.getPaymentMethod() != null
                                                        && b.getPaymentMethod().getValue() != null)
                                                                        ? b.getPaymentMethod().getValue()
                                                                        : "Cash"));

                        List<PaymentSplitDTO> paymentSplit = paymentsByMethod.entrySet().stream().map(entry -> {
                                BigDecimal amount = entry.getValue().stream()
                                                .map(calculateBillAmount)
                                                .reduce(BigDecimal.ZERO, BigDecimal::add);
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
                                        .map(entry -> {
                                                        MenuItem item = entry.getKey();
                                                        String imageStr = null;
                                                        if (item.getItemImage() != null && item.getItemImage().length > 0) {
                                                            String temp = new String(item.getItemImage(), java.nio.charset.StandardCharsets.UTF_8);
                                                            if (temp.startsWith("data:image") || temp.startsWith("http://") || temp.startsWith("https://")) {
                                                                imageStr = temp;
                                                            } else {
                                                                imageStr = Base64.getEncoder().encodeToString(item.getItemImage());
                                                            }
                                                        }
                                                        return FastMovingItemDTO.builder()
                                                                        .itemName(item.getItemName())
                                                                        .outletName(item.getOutlet() != null
                                                                                        ? item.getOutlet().getName()
                                                                                        : "N/A")
                                                                        .soldQty(entry.getValue())
                                                                        .imageUrl(imageStr)
                                                                        .itemImage(item.getItemImage())
                                                                        .build();
                                        })
                                        .collect(Collectors.toList());

                        // 6. Billing Watch
                        List<PosBill> allBills = posBillRepository.findAll();
                        List<PosBill> openBills = allBills.stream()
                                        .filter(b -> b.getStatus() == null
                                                        || "OPEN".equalsIgnoreCase(b.getStatus().getValue())
                                                        || "PENDING".equalsIgnoreCase(b.getStatus().getValue())
                                                        || "PARTIAL".equalsIgnoreCase(b.getStatus().getValue()))
                                        .collect(Collectors.toList());

                        int openBillsCount = openBills.size();
                        BigDecimal openBillsAmount = openBills.stream()
                                        .map(calculateBillAmount)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                        List<PosBill> roomChargeBills = allBills.stream()
                                        .filter(b -> Boolean.TRUE.equals(b.getPostToFolio())
                                                        || (b.getPaymentMethod() != null
                                                                        && b.getPaymentMethod().getValue() != null
                                                                        && b.getPaymentMethod().getValue().toUpperCase()
                                                                                        .contains("ROOM")))
                                        .collect(Collectors.toList());

                        int roomPostingPending = roomChargeBills.size();
                        BigDecimal roomPostingAmount = roomChargeBills.stream()
                                        .map(calculateBillAmount)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                        List<PosBill> voidBills = allBills.stream()
                                        .filter(b -> b.getStatus() != null
                                                        && "VOID".equalsIgnoreCase(b.getStatus().getValue()))
                                        .collect(Collectors.toList());

                        int voidsCount = voidBills.size();
                        BigDecimal voidsAmount = voidBills.stream()
                                        .map(calculateBillAmount)
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
                        nonVoidBills.stream().sorted(Comparator.comparing(PosBill::getCreatedAt).reversed()).limit(2)
                                        .forEach(b -> recentActivity.add(RecentActivityDTO.builder()
                                                        .activityType("Bill settled by " + (b.getPaymentMethod() != null
                                                                        ? b.getPaymentMethod().getValue()
                                                                        : "N/A"))
                                                        .linkedEntityId("BILL-" + b.getId())
                                                        .timestamp(b.getCreatedAt())
                                                        .build()));

                        PosDashboardCardsDTO cards = getPosDashboardCards(null, null, null).getData();

                        PosOpsDashboardDTO dashboardDTO = PosOpsDashboardDTO.builder()
                                        .cards(cards)
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

        @Override
        public StandardResponse<PosDashboardCardsDTO> getPosDashboardCards(Long outletId, LocalDateTime startDate,
                        LocalDateTime endDate) {
                try {
                        // 1. Active Outlets Count
                        List<Outlet> outlets = outletRepository.findByIsActiveTrue();
                        if (outletId != null) {
                                outlets = outlets.stream()
                                                .filter(o -> o.getId().equals(outletId))
                                                .collect(Collectors.toList());
                        }
                        int activeOutlets = outlets.size();

                        // Fetch POS Orders filtered by outlet and date range
                        List<PosOrder> allOrders = posOrderRepository.findAll();
                        List<PosOrder> filteredOrders = allOrders.stream()
                                        .filter(o -> Boolean.FALSE.equals(o.getIsDeleted()))
                                        .filter(o -> outletId == null || (o.getOutlet() != null
                                                        && outletId.equals(o.getOutlet().getId())))
                                        .filter(o -> startDate == null || (o.getCreatedAt() != null
                                                        && !o.getCreatedAt().isBefore(startDate)))
                                        .filter(o -> endDate == null || (o.getCreatedAt() != null
                                                        && !o.getCreatedAt().isAfter(endDate)))
                                        .collect(Collectors.toList());

                        // 2. Open Orders Count
                        int openOrders = (int) filteredOrders.stream()
                                        .filter(o -> o.getStatus() != null
                                                        && "OPEN".equalsIgnoreCase(o.getStatus().getValue()))
                                        .count();

                        // 3. KOT Running Count
                        int kotRunning = (int) filteredOrders.stream()
                                        .filter(o -> (o.getKotStatus() != null
                                                        && ("KOT_SENT".equalsIgnoreCase(o.getKotStatus().getCode())
                                                                        || "KOT_SENT".equalsIgnoreCase(
                                                                                        o.getKotStatus().getValue())))
                                                        ||
                                                        (o.getStatus() != null && ("KOT_SENT"
                                                                        .equalsIgnoreCase(o.getStatus().getValue())
                                                                        || "HELD".equalsIgnoreCase(
                                                                                        o.getStatus().getValue()))))
                                        .count();

                        // Fetch POS Bills filtered by outlet and date range
                        List<PosBill> allBills = posBillRepository.findByIsDeletedFalse();
                        List<PosBill> filteredBills = allBills.stream()
                                        .filter(b -> outletId == null || (b.getOrder() != null
                                                        && b.getOrder().getOutlet() != null
                                                        && outletId.equals(b.getOrder().getOutlet().getId())))
                                        .filter(b -> startDate == null || (b.getCreatedAt() != null
                                                        && !b.getCreatedAt().isBefore(startDate)))
                                        .filter(b -> endDate == null || (b.getCreatedAt() != null
                                                        && !b.getCreatedAt().isAfter(endDate)))
                                        .collect(Collectors.toList());

                        // 4. Total Bills Count
                        int billsCount = filteredBills.size();

                        // 5. Room Postings Count
                        int roomPostingsCount = (int) filteredBills.stream()
                                        .filter(b -> Boolean.TRUE.equals(b.getPostToFolio()) ||
                                                        (b.getPaymentMethod() != null && ("ROOM_CHARGE"
                                                                        .equalsIgnoreCase(
                                                                                        b.getPaymentMethod().getValue())
                                                                        || "ROOM_CHARGE".equalsIgnoreCase(
                                                                                        b.getPaymentMethod()
                                                                                                        .getCode()))))
                                        .count();

                        // 6. Gross Sales Total
                        BigDecimal grossSales = filteredBills.stream()
                                        .map(PosBill::getNetAmount)
                                        .filter(Objects::nonNull)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                        PosDashboardCardsDTO cardsDTO = PosDashboardCardsDTO.builder()
                                        .activeOutlets(activeOutlets)
                                        .openOrders(openOrders)
                                        .kotRunning(kotRunning)
                                        .bills(billsCount)
                                        .roomPostings(roomPostingsCount)
                                        .grossSales(grossSales)
                                        .build();

                        return StandardResponse.success(cardsDTO, "POS Dashboard Cards data fetched successfully");
                } catch (Exception e) {
                        log.error("Error fetching POS dashboard cards: ", e);
                        return StandardResponse.error("Failed to fetch POS dashboard cards", "INTERNAL_SERVER_ERROR",
                                        e.getMessage());
                }
        }
}
