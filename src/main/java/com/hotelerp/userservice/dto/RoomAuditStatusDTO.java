package com.hotelerp.userservice.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class RoomAuditStatusDTO {
    private Long roomId;
    private String roomNumber;
    private String roomType;
    private String pmsStatus;
    private String hkStatus;
    private String inspectorName;
    private LocalDateTime lastAuditDate;
    private Integer overallScore;
    private List<CheckpointStatusDTO> checkpoints;
}

