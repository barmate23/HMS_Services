package com.hotelerp.userservice.dto;

import lombok.Data;
import java.util.List;

@Data
public class RoomAuditSaveRequest {
    private Long roomId;
    private Long inspectorId;
    private Integer overallScore;
    private List<CheckpointAuditRequest> checkpoints;
}

