package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.config.LoginUser;
import com.hotelerp.userservice.dto.RoomAssignmentDTO;
import com.hotelerp.userservice.dto.StaffDTO;
import com.hotelerp.userservice.dto.TaskDTO;
import com.hotelerp.userservice.entity.Room;
import com.hotelerp.userservice.entity.Task;
import com.hotelerp.userservice.entity.User;
import com.hotelerp.userservice.entity.UserRoomMap;
import com.hotelerp.userservice.repository.RoomRepository;
import com.hotelerp.userservice.repository.TaskRepository;
import com.hotelerp.userservice.repository.UserRepository;
import com.hotelerp.userservice.repository.UserRoomMapRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StaffServiceImpl implements StaffService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final UserRoomMapRepository userRoomMapRepository;
    private final RoomRepository roomRepository;
    private final LoginUser loginUser;

    @Override
    public StandardResponse<List<StaffDTO>> getHousekeepingStaff() {
        try {
            Long hotelId = loginUser != null ? loginUser.getHotelId() : null;
            // Fetching staff from 'Housekeeping' department filtered by hotel property
            List<User> staffList = userRepository.findByDepartmentValueAndPropertyId("Housekeeping", hotelId);
            
            // Fetch tasks filtered by hotelId and isDeleted=false
            List<Task> allHotelTasks = hotelId != null 
                    ? taskRepository.findByHotel_IdAndIsDeletedFalse(hotelId)
                    : taskRepository.findByIsDeletedFalse();

            LocalDateTime startOfDay = LocalDate.now().atStartOfDay();

            List<StaffDTO> dtos = staffList.stream().map(user -> {
                // Task Analysis
                List<Task> userTasks = allHotelTasks.stream()
                        .filter(t -> t.getAssignedHousekeeper() != null && t.getAssignedHousekeeper().getId().equals(user.getId()))
                        .collect(Collectors.toList());

                long completedToday = userTasks.stream()
                        .filter(t -> t.getStatus() != null && "COMPLETED".equals(t.getStatus().getCode()) && 
                                    t.getUpdatedAt() != null && t.getUpdatedAt().isAfter(startOfDay))
                        .count();

                List<Task> pendingTasks = userTasks.stream()
                        .filter(t -> t.getStatus() == null || !"COMPLETED".equals(t.getStatus().getCode()))
                        .collect(Collectors.toList());

                // Room Mapping Analysis filtered by hotelId
                List<UserRoomMap> roomMappings = hotelId != null
                        ? userRoomMapRepository.findByHotel_IdAndUserId(hotelId, user.getId())
                        : userRoomMapRepository.findByUserId(user.getId());

                List<RoomAssignmentDTO> roomDetails = roomMappings.stream().map(mapping -> {
                    Room room = mapping.getRoom();
                    return RoomAssignmentDTO.builder()
                            .id(room != null ? room.getId() : null)
                            .roomNumber(room != null ? room.getRoomNumber() : null)
                            .roomTypeName(room != null && room.getRoomType() != null ? room.getRoomType().getName() : "Unknown")
                            .status(room != null && room.getStatus() != null ? room.getStatus().getValue() : "UNKNOWN")
                            .assignedUserId(user.getId())
                            .assignedUserName(user.getFullName())
                            .isAssignedToCurrentUser(true)
                            .build();
                }).collect(Collectors.toList());

                return StaffDTO.builder()
                        .id(user.getId())
                        .fullName(user.getFullName())
                        .role(user.getRole() != null ? user.getRole().getName() : "Unknown")
                        .status(user.getStatus())
                        .shift(user.getShift() != null ? user.getShift().getShiftName() : "Unknown")
                        .phone(user.getPhone())
                        .completedToday((int) completedToday)
                        .pendingTasks(pendingTasks.size())
                        .pendingTaskDetails(pendingTasks.stream().map(this::convertToTaskDTO).collect(Collectors.toList()))
                        .assignedRoomDetails(roomDetails)
                        .build();
            }).collect(Collectors.toList());

            return StandardResponse.success(dtos, "Housekeeping staff fetched successfully");
        } catch (Exception e) {
            log.error("Error fetching housekeeping staff: ", e);
            return StandardResponse.error("Failed to fetch housekeeping staff", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    private TaskDTO convertToTaskDTO(Task task) {
        return TaskDTO.builder()
                .id(task.getId())
                .roomId(task.getRoom() != null ? task.getRoom().getId() : null)
                .roomNumber(task.getRoom() != null ? task.getRoom().getRoomNumber() : null)
                .floorNumber(task.getRoom() != null && task.getRoom().getFloor() != null ? task.getRoom().getFloor().getFloorNumber() : null)
                .taskType(task.getTaskType())
                .priority(task.getPriority())
                .assignedUserId(task.getAssignedHousekeeper() != null ? task.getAssignedHousekeeper().getId() : null)
                .assignedUserName(task.getAssignedHousekeeper() != null ? task.getAssignedHousekeeper().getFullName() : null)
                .estimatedMinutes(task.getEstimatedMinutes())
                .instructions(task.getInstructions())
                .statusId(task.getStatus() != null ? task.getStatus().getId() : null)
                .status(task.getStatus() != null ? task.getStatus().getValue() : null)
                .build();
    }
}
