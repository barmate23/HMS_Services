package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.config.LoginUser;
import com.hotelerp.userservice.dto.UserRoomAssignmentRequest;
import com.hotelerp.userservice.entity.Hotel;
import com.hotelerp.userservice.entity.Room;
import com.hotelerp.userservice.entity.User;
import com.hotelerp.userservice.entity.UserRoomMap;
import com.hotelerp.userservice.exception.ResourceNotFoundException;
import com.hotelerp.userservice.repository.HotelRepository;
import com.hotelerp.userservice.repository.RoomRepository;
import com.hotelerp.userservice.repository.UserRepository;
import com.hotelerp.userservice.repository.UserRoomMapRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserRoomMapServiceImpl implements UserRoomMapService {

    private final UserRoomMapRepository userRoomMapRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final LoginUser loginUser;

    @Override
    @Transactional
    public StandardResponse<Void> syncUserRooms(UserRoomAssignmentRequest request) {
        try {
            Long hotelId = loginUser != null ? loginUser.getHotelId() : null;

            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + request.getUserId()));

            Hotel hotel = null;
            if (hotelId != null) {
                hotel = hotelRepository.findById(hotelId)
                        .orElseThrow(() -> new RuntimeException("Hotel not found"));
            }

            List<UserRoomMap> currentMappings;
            if (hotelId != null) {
                currentMappings = userRoomMapRepository.findByHotel_IdAndUserId(hotelId, user.getId());
            } else {
                currentMappings = userRoomMapRepository.findByUserId(user.getId());
            }

            List<Long> currentRoomIds = currentMappings.stream()
                    .map(m -> m.getRoom().getId())
                    .collect(Collectors.toList());

            List<Long> newRoomIds = request.getRoomIds();

            // Rooms to remove: in current but not in new
            List<UserRoomMap> toRemove = currentMappings.stream()
                    .filter(m -> !newRoomIds.contains(m.getRoom().getId()))
                    .collect(Collectors.toList());
            if (!toRemove.isEmpty()) {
                userRoomMapRepository.deleteAll(toRemove);
            }

            // Rooms to add: in new but not in current
            final Hotel finalHotel = hotel;
            for (Long roomId : newRoomIds) {
                if (!currentRoomIds.contains(roomId)) {
                    Room room = roomRepository.findById(roomId)
                            .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + roomId));

                    UserRoomMap mapping = UserRoomMap.builder()
                            .user(user)
                            .room(room)
                            .hotel(finalHotel)
                            .assignedAt(LocalDateTime.now())
                            .assignedBy(request.getAssignedBy())
                            .build();
                    userRoomMapRepository.save(mapping);
                }
            }

            return StandardResponse.success("User rooms synchronized successfully");
        } catch (ResourceNotFoundException e) {
            return StandardResponse.error(e.getMessage(), "RESOURCE_NOT_FOUND", e.getMessage());
        } catch (Exception e) {
            log.error("Error syncing user rooms: ", e);
            return StandardResponse.error("Failed to sync user rooms", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }
}
