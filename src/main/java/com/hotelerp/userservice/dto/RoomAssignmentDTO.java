package com.hotelerp.userservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomAssignmentDTO {
    private Long id;
    private String roomNumber;
    private String roomTypeName;
    private String status;
    private Long assignedUserId;
    private String assignedUserName;
    private boolean isAssignedToOther;
    private boolean isAssignedToCurrentUser;
}

