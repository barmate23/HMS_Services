package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.dashboard.*;
import com.hotelerp.common.entity.*;
import com.hotelerp.userservice.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final RoomRepository roomRepository;
    private final FloorRepository floorRepository;
    private final RoomBookingRepository roomBookingRepository;
    private final PosOrderRepository posOrderRepository;
    private final MenuItemRepository menuItemRepository;

    @Override
    public StandardResponse<DashboardDTO> getDashboardData(String financialYear) {
        try {
            // 1. Calculate Date Range for Financial Year
            // Format: "FY 2026-27"
            int startYear = Integer.parseInt(financialYear.split(" ")[1].split("-")[0]);
            LocalDateTime startDate = LocalDateTime.of(startYear, Month.APRIL, 1, 0, 0);
            LocalDateTime endDate = LocalDateTime.of(startYear + 1, Month.MARCH, 31, 23, 59, 59);

            // 2. Fetch Data
            List<Room> allRooms = roomRepository.findAll();
            List<Floor> allFloors = floorRepository.findAll();
            List<RoomBooking> bookings = roomBookingRepository.findAllInDateRange(startDate, endDate);
            List<PosOrder> posOrders = posOrderRepository.findAllInDateRange(startDate, endDate);

            // 3. Summary Stats
            int totalRooms = allRooms.size();
            int occupiedRooms = (int) allRooms.stream()
                    .filter(r -> isStatus(r.getStatus(), "OCCUPIED"))
                    .count();
            int availableRooms = (int) allRooms.stream()
                    .filter(r -> isStatus(r.getStatus(), "VACANT"))
                    .count();
            
            BigDecimal fyBookingRevenue = bookings.stream()
                    .map(b -> b.getTotalAmount() != null ? b.getTotalAmount() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            DashboardSummaryDTO summary = DashboardSummaryDTO.builder()
                    .totalRooms(totalRooms)
                    .availableRooms(availableRooms)
                    .occupiedRooms(occupiedRooms)
                    .fyBookingRevenue(fyBookingRevenue)
                    .posOrders(posOrders.size())
                    .build();

            // 4. Revenue & Bookings (Monthly)
            List<MonthlyStatDTO> monthlyPerformance = calculateMonthlyPerformance(bookings, startYear);
            int totalBookingsCount = bookings.size();
            BigDecimal abv = totalBookingsCount > 0 
                    ? fyBookingRevenue.divide(BigDecimal.valueOf(totalBookingsCount), 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            RevenueAndBookingsDTO revenueAndBookings = RevenueAndBookingsDTO.builder()
                    .monthlyPerformance(monthlyPerformance)
                    .totalRevenue(fyBookingRevenue)
                    .totalBookings(totalBookingsCount)
                    .abv(abv)
                    .build();

            // 5. Floor-wise Rooms
            List<FloorStatDTO> floorWiseRooms = allFloors.stream().map(floor -> {
                List<Room> roomsOnFloor = allRooms.stream()
                        .filter(r -> r.getFloor().getId().equals(floor.getId()))
                        .collect(Collectors.toList());
                
                int total = roomsOnFloor.size();
                int available = (int) roomsOnFloor.stream().filter(r -> isStatus(r.getStatus(), "VACANT")).count();
                int occupied = (int) roomsOnFloor.stream().filter(r -> isStatus(r.getStatus(), "OCCUPIED")).count();
                int blocked = (int) roomsOnFloor.stream().filter(r -> isStatus(r.getStatus(), "MAINTENANCE")).count();
                
                return FloorStatDTO.builder()
                        .floorName(floor.getFloorNumber())
                        .total(total)
                        .available(available)
                        .occupied(occupied)
                        .blocked(blocked)
                        .build();
            }).collect(Collectors.toList());

            double overallOccupancy = totalRooms > 0 ? (double) occupiedRooms / totalRooms * 100 : 0;

            // 6. POS Performance
            BigDecimal totalPosValue = posOrders.stream()
                    .map(o -> o.getTotalAmount() != null ? o.getTotalAmount() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal avgOrder = posOrders.size() > 0 
                    ? totalPosValue.divide(BigDecimal.valueOf(posOrders.size()), 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            // Sample top selling items (in real case, we'd query PosOrderItem for better accuracy)
            List<PosItemStatDTO> topSellingItems = calculateTopSellingItems(posOrders, true);
            List<PosItemStatDTO> lessSellingItems = calculateTopSellingItems(posOrders, false);

            PosPerformanceDTO posPerformance = PosPerformanceDTO.builder()
                    .orderValue(totalPosValue)
                    .avgOrder(avgOrder)
                    .menuItemsCount((int) menuItemRepository.count())
                    .topSellingItems(topSellingItems)
                    .lessSellingItems(lessSellingItems)
                    .build();

            DashboardDTO dashboardDTO = DashboardDTO.builder()
                    .summary(summary)
                    .revenueAndBookings(revenueAndBookings)
                    .floorWiseRooms(floorWiseRooms)
                    .overallOccupancy(Math.round(overallOccupancy * 100.0) / 100.0)
                    .posPerformance(posPerformance)
                    .build();

            return StandardResponse.success(dashboardDTO,"Dashboard data fetched successfully");

        } catch (Exception e) {
            return StandardResponse.error("Failed to fetch dashboard data: " ,"Internal_server_error", e.getMessage());
        }
    }

    private List<MonthlyStatDTO> calculateMonthlyPerformance(List<RoomBooking> bookings, int startYear) {
        String[] months = {"APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC", "JAN", "FEB", "MAR"};
        List<MonthlyStatDTO> result = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            final int monthIndex = (i + 3) % 12 + 1; // April is 4, ..., March is 3
            final int year = (i < 9) ? startYear : startYear + 1;
            
            List<RoomBooking> monthlyBookings = bookings.stream()
                    .filter(b -> b.getBookingDate().getMonthValue() == monthIndex && b.getBookingDate().getYear() == year)
                    .collect(Collectors.toList());

            BigDecimal revenue = monthlyBookings.stream()
                    .map(b -> b.getTotalAmount() != null ? b.getTotalAmount() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            result.add(MonthlyStatDTO.builder()
                    .month(months[i])
                    .revenue(revenue)
                    .bookings(monthlyBookings.size())
                    .build());
        }
        return result;
    }

    private List<PosItemStatDTO> calculateTopSellingItems(List<PosOrder> orders, boolean top) {
        // Flat map to items and group by menu item
        Map<MenuItem, List<PosOrderItem>> groupedItems = orders.stream()
                .flatMap(o -> o.getItems().stream())
                .collect(Collectors.groupingBy(PosOrderItem::getMenuItem));

        List<PosItemStatDTO> itemStats = groupedItems.entrySet().stream()
                .map(entry -> {
                    MenuItem item = entry.getKey();
                    List<PosOrderItem> items = entry.getValue();
                    int totalQty = items.stream().mapToInt(PosOrderItem::getQuantity).sum();
                    BigDecimal totalVal = items.stream()
                            .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    return PosItemStatDTO.builder()
                            .itemName(item.getItemName())
                            .category(item.getCategory() != null ? item.getCategory().getValue() : "N/A")
                            .soldQty(totalQty)
                            .rate(item.getPrice())
                            .avgRate(totalQty > 0 ? totalVal.divide(BigDecimal.valueOf(totalQty), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO)
                            .totalValue(totalVal)
                            .imageUrl(null) // image is byte array in DB
                            .monthlyTrend(new ArrayList<>()) // Placeholder
                            .build();
                })
                .sorted((a, b) -> top ? b.getSoldQty() - a.getSoldQty() : a.getSoldQty() - b.getSoldQty())
                .limit(5)
                .collect(Collectors.toList());

        return itemStats;
    }

    private boolean isStatus(CommonMaster status, String expected) {
        return status != null && expected.equalsIgnoreCase(status.getValue());
    }
}
