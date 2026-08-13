package com.hotelerp.userservice.controller;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.RoomReservationDTO;
import com.hotelerp.userservice.service.RoomReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hmsService/v1/reservation")
@RequiredArgsConstructor
public class RoomReservationController {

    private final RoomReservationService roomReservationService;

    /**
     * GET /api/hmsService/v1/reservation/active?roomId={roomId}
     *
     * Returns the currently active reservation for the specified room
     * (i.e., a booking whose checkInDate <= today < checkOutDate).
     *
     * @param roomId the primary key of the room
     * @return 200 OK with {@link RoomReservationDTO} on success,
     *         or a structured error response when no active reservation exists.
     */
    @GetMapping("/active")
    public ResponseEntity<StandardResponse<RoomReservationDTO>> getActiveReservation(
            @RequestParam Long roomId) {

        StandardResponse<RoomReservationDTO> response =
                roomReservationService.getActiveReservationByRoomId(roomId);
        return ResponseEntity.ok(response);
    }
}
