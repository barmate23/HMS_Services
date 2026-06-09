package com.hotelerp.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffDTO {
    private Long id;
    private String fullName;
    private String role;
    private String status; // ON DUTY, ON BREAK, OFF DUTY
    private String shift;
    private String phone;
    private Integer completedToday;
    private Integer pendingTasks;
    private List<TaskDTO> pendingTaskDetails;
    private List<RoomAssignmentDTO> assignedRoomDetails;
}


