package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.UserRoomAssignmentRequest;

public interface UserRoomMapService {
    StandardResponse<Void> syncUserRooms(UserRoomAssignmentRequest request);
}
