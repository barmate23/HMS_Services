package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.UserRoomAssignmentRequest;
import com.hotelerp.common.entity.Room;
import com.hotelerp.common.entity.User;
import com.hotelerp.common.entity.UserRoomMap;
import com.hotelerp.userservice.exception.ResourceNotFoundException;
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

    @Override
    @Transactional
    public StandardResponse<Void> syncUserRooms(UserRoomAssignmentRequest request) {
        try {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + request.getUserId()));

            List<UserRoomMap> currentMappings = userRoomMapRepository.findByUserId(user.getId());
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
            for (Long roomId : newRoomIds) {
                if (!currentRoomIds.contains(roomId)) {
                    Room room = roomRepository.findById(roomId)
                            .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + roomId));

                    UserRoomMap mapping = UserRoomMap.builder()
                            .user(user)
                            .room(room)
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
