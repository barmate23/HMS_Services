package com.hotelerp.userservice.service;

import com.hotelerp.userservice.dto.TaskDTO;
import com.hotelerp.userservice.entity.Room;
import com.hotelerp.userservice.entity.Task;
import com.hotelerp.userservice.entity.User;
import com.hotelerp.userservice.repository.RoomRepository;
import com.hotelerp.userservice.repository.TaskRepository;
import com.hotelerp.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public TaskDTO createTask(TaskDTO taskDTO) {
        Room room = roomRepository.findById(taskDTO.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + taskDTO.getRoomId()));

        User assignedUser = null;
        if (taskDTO.getAssignedUserId() != null) {
            assignedUser = userRepository.findById(taskDTO.getAssignedUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + taskDTO.getAssignedUserId()));
        }

        Task task = Task.builder()
                .room(room)
                .taskType(taskDTO.getTaskType())
                .priority(taskDTO.getPriority())
                .assignedHousekeeper(assignedUser)
                .estimatedMinutes(taskDTO.getEstimatedMinutes())
                .instructions(taskDTO.getInstructions())
                .status(Task.TaskStatus.PENDING) // Default to PENDING
                .build();

        Task savedTask = taskRepository.save(task);
        return convertToDTO(savedTask);
    }

    @Override
    @Transactional
    public TaskDTO updateTask(Long id, TaskDTO taskDTO) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

        if (taskDTO.getRoomId() != null) {
            Room room = roomRepository.findById(taskDTO.getRoomId())
                    .orElseThrow(() -> new RuntimeException("Room not found with id: " + taskDTO.getRoomId()));
            task.setRoom(room);
        }

        if (taskDTO.getAssignedUserId() != null) {
            User assignedUser = userRepository.findById(taskDTO.getAssignedUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + taskDTO.getAssignedUserId()));
            task.setAssignedHousekeeper(assignedUser);
        }

        task.setTaskType(taskDTO.getTaskType());
        task.setPriority(taskDTO.getPriority());
        task.setEstimatedMinutes(taskDTO.getEstimatedMinutes());
        task.setInstructions(taskDTO.getInstructions());
        if (taskDTO.getStatus() != null) {
            task.setStatus(taskDTO.getStatus());
        }

        Task updatedTask = taskRepository.save(task);
        return convertToDTO(updatedTask);
    }

    @Override
    public TaskDTO getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        return convertToDTO(task);
    }

    @Override
    public List<TaskDTO> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    @Override
    @Transactional
    public TaskDTO updateStatus(Long id, String status) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        
        task.setStatus(Task.TaskStatus.valueOf(status.toUpperCase()));
        Task updatedTask = taskRepository.save(task);
        return convertToDTO(updatedTask);
    }

    private TaskDTO convertToDTO(Task task) {
        return TaskDTO.builder()
                .id(task.getId())
                .roomId(task.getRoom().getId())
                .roomNumber(task.getRoom().getRoomNumber())
                .floorNumber(task.getRoom().getFloor() != null ? task.getRoom().getFloor().getFloorNumber() : null)
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
