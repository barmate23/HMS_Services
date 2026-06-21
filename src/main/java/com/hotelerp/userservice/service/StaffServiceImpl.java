package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
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

    @Override
    public StandardResponse<List<StaffDTO>> getHousekeepingStaff() {
        try {
            // Fetching staff from 'Housekeeping' department
            List<User> staffList = userRepository.findByDepartmentValue("Housekeeping");
            
            List<StaffDTO> dtos = staffList.stream().map(user -> {
                // Task Analysis
                LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
                List<Task> userTasks = taskRepository.findAll().stream()
                        .filter(t -> t.getAssignedHousekeeper() != null && t.getAssignedHousekeeper().getId().equals(user.getId()))
                        .collect(Collectors.toList());

                long completedToday = userTasks.stream()
                        .filter(t -> t.getStatus() != null && "COMPLETED".equals(t.getStatus().getCode()) && 
                                    t.getUpdatedAt() != null && t.getUpdatedAt().isAfter(startOfDay))
                        .count();

                List<Task> pendingTasks = userTasks.stream()
                        .filter(t -> t.getStatus() == null || !"COMPLETED".equals(t.getStatus().getCode()))
                        .collect(Collectors.toList());

                // Room Mapping Analysis
                List<UserRoomMap> roomMappings = userRoomMapRepository.findByUserId(user.getId());
                List<RoomAssignmentDTO> roomDetails = roomMappings.stream().map(mapping -> {
                    Room room = mapping.getRoom();
                    return RoomAssignmentDTO.builder()
                            .id(room.getId())
                            .roomNumber(room.getRoomNumber())
                            .roomTypeName(room.getRoomType() != null ? room.getRoomType().getName() : "Unknown")
                            .status(room.getStatus() != null ? room.getStatus().getValue() : "UNKNOWN")
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
                .roomId(task.getRoom().getId())
                .roomNumber(task.getRoom().getRoomNumber())
                .floorNumber(task.getRoom().getFloor().getFloorNumber())
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
