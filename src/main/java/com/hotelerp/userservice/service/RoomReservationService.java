package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.RoomReservationDTO;

public interface RoomReservationService {

    /**
     * Returns the currently active reservation details for the given room.
     *
     * @param roomId the primary key of the room
     * @return StandardResponse wrapping {@link RoomReservationDTO}
     */
    StandardResponse<RoomReservationDTO> getActiveReservationByRoomId(Long roomId);
}
