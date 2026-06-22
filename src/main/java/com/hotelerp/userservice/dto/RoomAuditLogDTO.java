package com.hotelerp.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomAuditLogDTO {
    private Long id;
    private Long roomId;
    private String roomNumber;
    private Long checkpointId;
    private String checkpointName;
    private String auditArea;
    private Long statusId;
    private String statusName;
    private Long inspectorId;
    private String inspectorName;
    private Integer score;
    private String remarks;
    private LocalDateTime auditDate;
}
