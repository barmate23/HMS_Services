package com.hotelerp.userservice.dto.hkdashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HousekeepingDashboardDTO {
    private HkSummaryDTO summary;
    private List<HkAttentionItemDTO> attentionQueue;
    private HkTeamLoadDTO teamLoad;
    private HkAuditReadinessDTO auditReadiness;
    private List<FloorRoomBoardDTO> floorRoomBoard;
}
