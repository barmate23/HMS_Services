package com.hotelerp.userservice.dto.hkdashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HkSummaryDTO {
    private int readyRooms;
    private int needService;
    private int blockedDnd;
    private int openTasks;
    private int repairIssues;
    private int sopChecks;
    private int readyPercentage;
}
