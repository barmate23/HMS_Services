package com.hotelerp.userservice.dto;

import com.hotelerp.userservice.entity.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDTO {
    private Long id;
    private Long roomId;
    private String roomNumber;
    private String floorNumber;
    private String taskType;
    private Task.Priority priority;
    private Long assignedUserId;
    private String assignedUserName;
    private Integer estimatedMinutes;
    private String instructions;
    private Long statusId;
    private String status;
}
