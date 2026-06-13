package com.hotelerp.userservice.dto.laundrydashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LaundryDashboardSummaryDTO {
    private Integer pendingPickup;
    private Integer inProcess;
    private Integer ready;
    private Double revenue;
    private Integer overdue;
    private Integer completedToday;
}
