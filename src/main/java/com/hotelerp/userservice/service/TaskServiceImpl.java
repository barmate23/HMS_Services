package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.TaskDTO;
import com.hotelerp.common.entity.Room;
import com.hotelerp.common.entity.Task;
import com.hotelerp.common.entity.User;
import com.hotelerp.userservice.repository.RoomRepository;
import com.hotelerp.userservice.repository.TaskRepository;
import com.hotelerp.userservice.repository.UserRepository;
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

    @Override
    @Transactional
    public StandardResponse<Void> createTask(TaskDTO taskDTO) {
        try {
            Long roomId = taskDTO.getRoomId();
            if (roomId == null) throw new IllegalArgumentException("Room ID must not be null");
            
            Room room = roomRepository.findById(roomId)
                    .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + roomId));

            User assignedUser = null;
            if (taskDTO.getAssignedUserId() != null) {
                assignedUser = userRepository.findById(taskDTO.getAssignedUserId())
                        .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + taskDTO.getAssignedUserId()));
            }

            Task task = Task.builder()
                    .room(room)
                    .taskType(taskDTO.getTaskType())
                    .priority(taskDTO.getPriority() != null ? taskDTO.getPriority() : Task.Priority.MEDIUM)
                    .assignedHousekeeper(assignedUser)
                    .estimatedMinutes(taskDTO.getEstimatedMinutes())
                    .instructions(taskDTO.getInstructions())
                    .status(taskDTO.getStatus() != null ? taskDTO.getStatus() : Task.TaskStatus.PENDING)
                    .build();

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
                        .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + taskDTO.getRoomId()));
                task.setRoom(room);
            }

            task.setTaskType(taskDTO.getTaskType());
            task.setPriority(taskDTO.getPriority());
            task.setEstimatedMinutes(taskDTO.getEstimatedMinutes());
            task.setInstructions(taskDTO.getInstructions());
            task.setStatus(taskDTO.getStatus());

            if (taskDTO.getAssignedUserId() != null) {
                User assignedUser = userRepository.findById(taskDTO.getAssignedUserId())
                        .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + taskDTO.getAssignedUserId()));
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
            List<TaskDTO> dtos = taskRepository.findAll().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return StandardResponse.success(dtos, "All tasks fetched successfully");
        } catch (Exception e) {
            log.error("Error fetching all tasks: ", e);
            return StandardResponse.error("Failed to fetch tasks", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    @Transactional
    public StandardResponse<Void> deleteTask(Long id) {
        try {
            if (!taskRepository.existsById(id)) {
                throw new ResourceNotFoundException("Task not found with ID: " + id);
            }
            taskRepository.deleteById(id);
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
            
            try {
                task.setStatus(Task.TaskStatus.valueOf(status.toUpperCase()));
            } catch (IllegalArgumentException e) {
                return StandardResponse.error("Invalid status: " + status, "INVALID_INPUT", "Allowed statuses: PENDING, IN_PROGRESS, COMPLETED");
            }
            
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
                .roomId(task.getRoom().getId())
                .roomNumber(task.getRoom().getRoomNumber())
                .floorNumber(task.getRoom().getFloor().getFloorNumber())
                .taskType(task.getTaskType())
                .priority(task.getPriority())
                .assignedUserId(task.getAssignedHousekeeper() != null ? task.getAssignedHousekeeper().getId() : null)
                .assignedUserName(task.getAssignedHousekeeper() != null ? task.getAssignedHousekeeper().getFullName() : null)
                .estimatedMinutes(task.getEstimatedMinutes())
                .instructions(task.getInstructions())
                .status(task.getStatus())
                .build();
    }
}
