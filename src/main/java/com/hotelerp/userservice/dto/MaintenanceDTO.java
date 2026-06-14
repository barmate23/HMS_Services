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
    private Long statusId;
    private String statusName;
    private LocalDateTime reportedAt;
}
