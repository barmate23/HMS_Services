package com.hotelerp.userservice.dto.laundrydashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LaundryDashboardDTO {
    private LaundryDashboardSummaryDTO summary;
    private List<LaundryDashboardActivityDTO> activityFeed;
}
