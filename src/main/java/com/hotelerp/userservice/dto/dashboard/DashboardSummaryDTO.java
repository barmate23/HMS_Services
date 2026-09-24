package com.hotelerp.userservice.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryDTO {
    private int totalRooms;
    private int totalBookings;
    private int availableRooms;
    private int occupiedRooms;
    private int bookedRooms;
    private int blockedRooms;
    private int underMaintenanceRooms;
    private BigDecimal fyBookingRevenue;
    private int posOrders;
}
