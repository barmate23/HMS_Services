package com.hotelerp.userservice.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO {
    private DashboardSummaryDTO summary;
    private RevenueAndBookingsDTO revenueAndBookings;
    private List<FloorStatDTO> floorWiseRooms;
    private double overallOccupancy;
    private PosPerformanceDTO posPerformance;
}
