package com.hotelerp.userservice.dto;

import lombok.Data;
import java.util.List;

@Data
public class UserRoomAssignmentRequest {
    private Long userId;
    private List<Long> roomIds;
    private String assignedBy;
}
