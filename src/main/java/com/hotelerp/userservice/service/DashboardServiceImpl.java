package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.config.LoginUser;
import com.hotelerp.userservice.dto.dashboard.*;
import com.hotelerp.userservice.entity.*;
import com.hotelerp.userservice.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final RoomRepository roomRepository;
    private final FloorRepository floorRepository;
    private final BookingRepository bookingRepository;
    private final PosOrderRepository posOrderRepository;
    private final MenuItemRepository menuItemRepository;
    private final LoginUser loginUser;

    @Override
    public StandardResponse<DashboardDTO> getDashboardData(String financialYear) {
        try {
            // 1. Calculate Date Range for Financial Year
            // Format: "FY 2026-27"
            int startYear = Integer.parseInt(financialYear.split(" ")[1].split("-")[0]);
            LocalDateTime startDate = LocalDateTime.of(startYear, Month.APRIL, 1, 0, 0);
            LocalDateTime endDate = LocalDateTime.of(startYear + 1, Month.MARCH, 31, 23, 59, 59);

            Long hotelId = loginUser != null ? loginUser.getHotelId() : null;

            // 2. Fetch Data
            List<Room> allRooms = (hotelId != null)
                    ? roomRepository.findByFloor_Hotel_IdAndIsDeletedFalse(hotelId)
                    : roomRepository.findByIsDeletedFalse();
            List<Floor> allFloors = (hotelId != null)
                    ? floorRepository.findByHotel_Id(hotelId)
                    : floorRepository.findAll();
            List<Booking> bookings = (hotelId != null)
                    ? bookingRepository.findAllInDateRangeAndHotelId(startDate, endDate, hotelId)
                    : bookingRepository.findAllInDateRange(startDate, endDate);
            List<PosOrder> posOrders = (hotelId != null)
                    ? posOrderRepository.findAllInDateRangeAndHotelId(startDate, endDate, hotelId)
                    : posOrderRepository.findAllInDateRange(startDate, endDate);

            List<Booking> activeBookings = bookingRepository.findAllActiveBookingsByDateAndHotel(hotelId);
            LocalDate today = LocalDate.now();
            Set<Long> occupiedRoomIds = activeBookings.stream()
                    .filter(b -> b.getRoom() != null && b.getRoom().getId() != null)
                    .filter(b -> {
                        LocalDate checkIn = b.getCheckInDate() != null ? b.getCheckInDate()
                                : (b.getReservation() != null ? b.getReservation().getCheckInDate() : null);
                        LocalDate checkOut = b.getCheckOutDate() != null ? b.getCheckOutDate()
                                : (b.getReservation() != null ? b.getReservation().getCheckOutDate() : null);
                        if (checkIn == null || checkOut == null) return false;
                        // Strictly for TODAY only (today is within [checkInDate, checkOutDate]) - no future dates
                        return !today.isBefore(checkIn) && !today.isAfter(checkOut);
                    })
                    .filter(b -> {
                        // Exclude cancelled or checked-out bookings
                        if (b.getBookingStatus() != null) {
                            String st = b.getBookingStatus().getCode() != null ? b.getBookingStatus().getCode().toUpperCase() : "";
                            if (st.contains("CANCEL") || st.contains("NO_SHOW") || st.contains("CHECKED_OUT") || st.contains("CHECK_OUT")) {
                                return false;
                            }
                        }
                        if (b.getReservation() != null && b.getReservation().getReservationStatus() != null) {
                            String st = b.getReservation().getReservationStatus().getCode() != null ? b.getReservation().getReservationStatus().getCode().toUpperCase() : "";
                            if (st.contains("CANCEL") || st.contains("NO_SHOW") || st.contains("CHECKED_OUT") || st.contains("CHECK_OUT")) {
                                return false;
                            }
                        }
                        return true;
                    })
                    .map(b -> b.getRoom().getId())
                    .collect(Collectors.toSet());

            // 3. Summary Stats
            int totalRooms = allRooms.size();
            int occupiedRooms = (int) allRooms.stream()
                    .filter(r -> isOccupied(r, occupiedRoomIds))
                    .count();
            int blockedRooms = (int) allRooms.stream()
                    .filter(r -> !isOccupied(r, occupiedRoomIds) && isBlocked(r))
                    .count();
            int availableRooms = Math.max(0, totalRooms - occupiedRooms - blockedRooms);
            
            BigDecimal fyBookingRevenue = bookings.stream()
                    .map(b -> b.getFinalPrice() != null ? b.getFinalPrice() : BigDecimal.ZERO)
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
                        .filter(r -> r.getFloor() != null && r.getFloor().getId() != null && r.getFloor().getId().equals(floor.getId()))
                        .collect(Collectors.toList());
                
                int total = roomsOnFloor.size();
                int occupied = (int) roomsOnFloor.stream().filter(r -> isOccupied(r, occupiedRoomIds)).count();
                int blocked = (int) roomsOnFloor.stream().filter(r -> !isOccupied(r, occupiedRoomIds) && isBlocked(r)).count();
                int available = Math.max(0, total - occupied - blocked);
                
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
            List<PosItemStatDTO> topSellingItems = calculateTopSellingItems(posOrders, true, startYear);
            List<PosItemStatDTO> lessSellingItems = calculateTopSellingItems(posOrders, false, startYear);

            PosPerformanceDTO posPerformance = PosPerformanceDTO.builder()
                    .orderValue(totalPosValue)
                    .avgOrder(avgOrder)
                    .menuItemsCount((hotelId != null) 
                            ? menuItemRepository.findByHotel_IdAndIsDeletedFalse(hotelId).size() 
                            : (int) menuItemRepository.count())
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

    private List<MonthlyStatDTO> calculateMonthlyPerformance(List<Booking> bookings, int startYear) {
        String[] months = {"APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC", "JAN", "FEB", "MAR"};
        List<MonthlyStatDTO> result = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            final int monthIndex = (i + 3) % 12 + 1; // April is 4, ..., March is 3
            final int year = (i < 9) ? startYear : startYear + 1;
            
            List<Booking> monthlyBookings = bookings.stream()
                    .filter(b -> b.getCreatedAt().getMonthValue() == monthIndex && b.getCreatedAt().getYear() == year)
                    .collect(Collectors.toList());

            BigDecimal revenue = monthlyBookings.stream()
                    .map(b -> b.getFinalPrice() != null ? b.getFinalPrice() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            result.add(MonthlyStatDTO.builder()
                    .month(months[i])
                    .revenue(revenue)
                    .bookings(monthlyBookings.size())
                    .build());
        }
        return result;
    }

    private List<PosItemStatDTO> calculateTopSellingItems(List<PosOrder> orders, boolean top, int startYear) {
        String[] monthNames = {"APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC", "JAN", "FEB", "MAR"};
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
                            .map(i -> i.getSubtotal() != null ? i.getSubtotal() : BigDecimal.ZERO)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    // Calculate monthly trend for this item
                    List<MonthlyStatDTO> monthlyTrend = new ArrayList<>();
                    for (int i = 0; i < 12; i++) {
                        final int monthIndex = (i + 3) % 12 + 1; // April is 4, ..., March is 3
                        final int year = (i < 9) ? startYear : startYear + 1;

                        List<PosOrderItem> monthlyItems = items.stream()
                                .filter(oi -> oi.getOrder() != null && oi.getOrder().getCreatedAt() != null &&
                                             oi.getOrder().getCreatedAt().getMonthValue() == monthIndex && 
                                             oi.getOrder().getCreatedAt().getYear() == year)
                                .collect(Collectors.toList());
                        
                        int monthlyQty = monthlyItems.stream().mapToInt(PosOrderItem::getQuantity).sum();
                        BigDecimal monthlyRev = monthlyItems.stream()
                                .map(oi -> oi.getSubtotal() != null ? oi.getSubtotal() : BigDecimal.ZERO)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                        monthlyTrend.add(MonthlyStatDTO.builder()
                                .month(monthNames[i])
                                .revenue(monthlyRev)
                                .soldQty(monthlyQty)
                                .build());
                    }

                    String imageStr = null;
                    if (item.getItemImage() != null && item.getItemImage().length > 0) {
                        String temp = new String(item.getItemImage(), java.nio.charset.StandardCharsets.UTF_8);
                        if (temp.startsWith("data:image") || temp.startsWith("http://") || temp.startsWith("https://")) {
                            imageStr = temp;
                        } else {
                            imageStr = Base64.getEncoder().encodeToString(item.getItemImage());
                        }
                    }

                    return PosItemStatDTO.builder()
                            .itemName(item.getItemName())
                            .category(item.getCategory() != null ? item.getCategory().getValue() : "N/A")
                            .soldQty(totalQty)
                            .rate(item.getPrice())
                            .avgRate(totalQty > 0 ? totalVal.divide(BigDecimal.valueOf(totalQty), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO)
                            .totalValue(totalVal)
                            .itemImage(item.getItemImage())
                            .monthlyTrend(monthlyTrend)
                            .build();
                })
                .sorted((a, b) -> top ? b.getSoldQty() - a.getSoldQty() : a.getSoldQty() - b.getSoldQty())
                .limit(5)
                .collect(Collectors.toList());

        return itemStats;
    }


    private boolean isOccupied(Room room, Set<Long> occupiedRoomIds) {
        if (room == null) return false;
        return occupiedRoomIds != null && occupiedRoomIds.contains(room.getId());
    }

    private boolean isBlocked(Room room) {
        if (room == null) return false;
        return matchesStatus(room.getStatus(), "MAINTENANCE", "BLOCKED", "OUT_OF_ORDER", "UNDER_MAINTENANCE")
                || matchesStatus(room.getHkStatus(), "MAINTENANCE", "DO NOT DISTURB", "DND", "BLOCKED", "UNDER MAINTENANCE");
    }

    private boolean matchesStatus(CommonMaster status, String... expectedKeywords) {
        if (status == null) return false;
        String code = status.getCode() != null ? status.getCode().toUpperCase() : "";
        String value = status.getValue() != null ? status.getValue().toUpperCase() : "";
        for (String kw : expectedKeywords) {
            String target = kw.toUpperCase();
            if (code.equals(target) || code.contains(target) || value.equals(target) || value.contains(target)) {
                return true;
            }
        }
        return false;
    }
}
