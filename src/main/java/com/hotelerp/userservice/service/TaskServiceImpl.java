package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.config.LoginUser;
import com.hotelerp.userservice.dto.TaskDTO;
import com.hotelerp.userservice.entity.Room;
import com.hotelerp.userservice.entity.Task;
import com.hotelerp.userservice.entity.User;
import com.hotelerp.userservice.entity.CommonMaster;
import com.hotelerp.userservice.repository.HotelRepository;
import com.hotelerp.userservice.repository.RoomRepository;
import com.hotelerp.userservice.repository.TaskRepository;
import com.hotelerp.userservice.repository.UserRepository;
import com.hotelerp.userservice.repository.CommonMasterRepository;
import com.hotelerp.userservice.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final CommonMasterRepository commonMasterRepository;
    private final HotelRepository hotelRepository;
    private final LoginUser loginUser;

    @Override
    @Transactional
    public StandardResponse<Void> createTask(TaskDTO taskDTO) {
        try {
            Long hotelId = (loginUser != null && loginUser.getHotelId() != null) ? loginUser.getHotelId() : taskDTO.getHotelId();

            Long roomId = taskDTO.getRoomId();
            if (roomId == null)
                throw new IllegalArgumentException("Room ID must not be null");

            Room room = roomRepository.findById(roomId)
                    .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + roomId));

            User assignedUser = null;
            if (taskDTO.getAssignedUserId() != null) {
                assignedUser = userRepository.findById(taskDTO.getAssignedUserId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "User not found with ID: " + taskDTO.getAssignedUserId()));
            }

            CommonMaster status = null;
            if (taskDTO.getStatusId() != null) {
                status = commonMasterRepository.findById(taskDTO.getStatusId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Status master not found with ID: " + taskDTO.getStatusId()));
            } else if (taskDTO.getStatus() != null) {
                status = commonMasterRepository
                        .findByCategoryAndCode("HOUSEKEEPING_STATUS", taskDTO.getStatus().toUpperCase())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Status master not found with code: " + taskDTO.getStatus()));
            } else {
                status = commonMasterRepository.findByCategoryAndCode("HOUSEKEEPING_STATUS", "PENDING")
                        .orElseThrow(() -> new ResourceNotFoundException("Default 'PENDING' status not found"));
            }

            Task task = Task.builder()
                    .room(room)
                    .taskType(taskDTO.getTaskType())
                    .priority(taskDTO.getPriority() != null ? taskDTO.getPriority() : Task.Priority.MEDIUM)
                    .assignedHousekeeper(assignedUser)
                    .estimatedMinutes(taskDTO.getEstimatedMinutes())
                    .instructions(taskDTO.getInstructions())
                    .status(status)
                    .build();

            if (hotelId != null) {
                task.setHotel(hotelRepository.findById(hotelId)
                        .orElseThrow(() -> new RuntimeException("Hotel not found")));
            }

            taskRepository.save(task);
            return StandardResponse.success("Housekeeping task created successfully");
        } catch (ResourceNotFoundException e) {
            return StandardResponse.error(e.getMessage(), "RESOURCE_NOT_FOUND", e.getMessage());
        } catch (Exception e) {
            log.error("Error creating task: ", e);
            return StandardResponse.error("Failed to create task", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    @Transactional
    public StandardResponse<TaskDTO> updateTask(Long id, TaskDTO taskDTO) {
        try {
            Task task = taskRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + id));

            if (taskDTO.getRoomId() != null) {
                Room room = roomRepository.findById(taskDTO.getRoomId())
                        .orElseThrow(
                                () -> new ResourceNotFoundException("Room not found with ID: " + taskDTO.getRoomId()));
                task.setRoom(room);
            }

            task.setTaskType(taskDTO.getTaskType());
            task.setPriority(taskDTO.getPriority());
            task.setEstimatedMinutes(taskDTO.getEstimatedMinutes());
            task.setInstructions(taskDTO.getInstructions());

            if (taskDTO.getStatusId() != null) {
                CommonMaster status = commonMasterRepository.findById(taskDTO.getStatusId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Status master not found for ID: " + taskDTO.getStatusId()));
                task.setStatus(status);
            } else if (taskDTO.getStatus() != null) {
                CommonMaster status = commonMasterRepository
                        .findByCategoryAndCode("HOUSEKEEPING_STATUS", taskDTO.getStatus().toUpperCase())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Status master not found for code: " + taskDTO.getStatus()));
                task.setStatus(status);
            }

            if (taskDTO.getAssignedUserId() != null) {
                User assignedUser = userRepository.findById(taskDTO.getAssignedUserId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "User not found with ID: " + taskDTO.getAssignedUserId()));
                task.setAssignedHousekeeper(assignedUser);
            }

            Task updatedTask = taskRepository.save(task);
            return StandardResponse.success(convertToDTO(updatedTask), "Task updated successfully");
        } catch (ResourceNotFoundException e) {
            return StandardResponse.error(e.getMessage(), "RESOURCE_NOT_FOUND", e.getMessage());
        } catch (Exception e) {
            log.error("Error updating task: ", e);
            return StandardResponse.error("Failed to update task", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    public StandardResponse<TaskDTO> getTaskById(Long id) {
        try {
            Task task = taskRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + id));
            return StandardResponse.success(convertToDTO(task), "Task fetched successfully");
        } catch (ResourceNotFoundException e) {
            return StandardResponse.error(e.getMessage(), "RESOURCE_NOT_FOUND", e.getMessage());
        } catch (Exception e) {
            log.error("Error fetching task: ", e);
            return StandardResponse.error("Failed to fetch task", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    public StandardResponse<List<TaskDTO>> getAllTasks() {
        try {
            Long hotelId = loginUser != null ? loginUser.getHotelId() : null;
            List<TaskDTO> dtos;
            if (hotelId != null) {
                dtos = taskRepository.findByHotel_IdAndIsDeletedFalse(hotelId).stream()
                        .map(this::convertToDTO)
                        .collect(Collectors.toList());
            } else {
                dtos = taskRepository.findAll().stream()
                        .filter(t -> !Boolean.TRUE.equals(t.getIsDeleted()))
                        .map(this::convertToDTO)
                        .collect(Collectors.toList());
            }
            return StandardResponse.success(dtos, "All tasks fetched successfully");
        } catch (Exception e) {
            log.error("Error fetching all tasks: ", e);
            return StandardResponse.error("Failed to fetch tasks", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    public StandardResponse<List<TaskDTO>> getActiveTasks() {
        try {
            Long hotelId = loginUser != null ? loginUser.getHotelId() : null;
            List<String> activeCodes = List.of("PENDING", "IN_PROGRESS");
            List<TaskDTO> dtos;
            if (hotelId != null) {
                dtos = taskRepository.findByHotel_IdAndStatus_CodeInAndIsDeletedFalse(hotelId, activeCodes).stream()
                        .map(this::convertToDTO)
                        .collect(Collectors.toList());
            } else {
                dtos = taskRepository.findByStatusCodeInAndIsDeletedFalse(activeCodes).stream()
                        .map(this::convertToDTO)
                        .collect(Collectors.toList());
            }
            return StandardResponse.success(dtos, "Active tasks fetched successfully");
        } catch (Exception e) {
            log.error("Error fetching active tasks: ", e);
            return StandardResponse.error("Failed to fetch active tasks", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    @Transactional
    public StandardResponse<Void> deleteTask(Long id) {
        try {
            Task task = taskRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + id));
            task.setIsDeleted(true);
            taskRepository.save(task);
            return StandardResponse.success("Task deleted successfully");
        } catch (ResourceNotFoundException e) {
            return StandardResponse.error(e.getMessage(), "RESOURCE_NOT_FOUND", e.getMessage());
        } catch (Exception e) {
            log.error("Error deleting task: ", e);
            return StandardResponse.error("Failed to delete task", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    @Transactional
    public StandardResponse<TaskDTO> updateStatus(Long id, String status) {
        try {
            Task task = taskRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + id));

            CommonMaster statusMaster = commonMasterRepository
                    .findByCategoryAndCode("HOUSEKEEPING_STATUS", status.toUpperCase())
                    .orElseThrow(() -> new ResourceNotFoundException("Status master not found for code: " + status));

            task.setStatus(statusMaster);
            Task updatedTask = taskRepository.save(task);
            return StandardResponse.success(convertToDTO(updatedTask), "Task status updated successfully");
        } catch (ResourceNotFoundException e) {
            return StandardResponse.error(e.getMessage(), "RESOURCE_NOT_FOUND", e.getMessage());
        } catch (Exception e) {
            log.error("Error updating task status: ", e);
            return StandardResponse.error("Failed to update status", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    private TaskDTO convertToDTO(Task task) {
        return TaskDTO.builder()
                .id(task.getId())
                .hotelId(task.getHotel() != null ? task.getHotel().getId() : null)
                .hotelName(task.getHotel() != null ? task.getHotel().getName() : null)
                .roomId(task.getRoom().getId())
                .roomNumber(task.getRoom().getRoomNumber())
                .floorNumber(task.getRoom().getFloor().getFloorNumber())
                .taskType(task.getTaskType())
                .priority(task.getPriority())
                .assignedUserId(task.getAssignedHousekeeper() != null ? task.getAssignedHousekeeper().getId() : null)
                .assignedUserName(
                        task.getAssignedHousekeeper() != null ? task.getAssignedHousekeeper().getFullName() : null)
                .estimatedMinutes(task.getEstimatedMinutes())
                .instructions(task.getInstructions())
                .statusId(task.getStatus() != null ? task.getStatus().getId() : null)
                .status(task.getStatus() != null ? task.getStatus().getCode() : null)
                .build();
    }
}
