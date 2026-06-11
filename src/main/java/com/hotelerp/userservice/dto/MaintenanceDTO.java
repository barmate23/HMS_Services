package com.hotelerp.userservice.dto;

import com.hotelerp.common.entity.MaintenanceRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceDTO {
    private Long id;
    private Long roomId;
    private String roomNumber;
    private String repairIssue;
    private Long categoryId;
    private String categoryValue;
    private Long priorityId;
    private String priorityValue;
    private Long reportedById;
    private String reportedByName;
    private Long assignedToId;
    private String assignedToName;
    private String repairNotes;
    private MaintenanceRequest.MaintenanceStatus status;
    private LocalDateTime reportedAt;
}
