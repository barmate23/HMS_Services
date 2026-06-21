package com.hotelerp.userservice.dto.hkdashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HkAuditReadinessDTO {
    private String activeSop;
    private int checkpoints;
    private int roomsTracked;
    private int pendingAudits;
    private int doneAudits;
    private int recheckAudits;
}
