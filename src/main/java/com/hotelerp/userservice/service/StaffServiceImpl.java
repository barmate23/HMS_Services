package com.hotelerp.userservice.service;

import com.hotelerp.userservice.dto.StaffDTO;
import com.hotelerp.userservice.entity.Task;
import com.hotelerp.userservice.entity.User;
import com.hotelerp.userservice.repository.TaskRepository;
import com.hotelerp.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    @Override
    public List<StaffDTO> getHousekeepingStaff() {
        // Fetching staff from 'Housekeeping' department
        List<User> staffList = userRepository.findByDepartment("Housekeeping");
        
        return staffList.stream().map(user -> {
            // Count completed tasks for today
            LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
            List<Task> userTasks = taskRepository.findAll().stream() // Simplified for now, should use a custom repo method for performance
                    .filter(t -> t.getAssignedHousekeeper() != null && t.getAssignedHousekeeper().getId().equals(user.getId()))
                    .collect(Collectors.toList());

            long completedToday = userTasks.stream()
                    .filter(t -> t.getStatus() == Task.TaskStatus.COMPLETED && 
                                t.getUpdatedAt() != null && t.getUpdatedAt().isAfter(startOfDay))
                    .count();

            List<String> assignedRooms = userTasks.stream()
                    .filter(t -> t.getStatus() != Task.TaskStatus.COMPLETED)
                    .map(t -> t.getRoom().getRoomNumber())
                    .distinct()
                    .collect(Collectors.toList());

            return StaffDTO.builder()
                    .id(user.getId())
                    .fullName(user.getFullName())
                    .role(user.getRole())
                    .status(user.getStatus())
                    .shift(user.getShift())
                    .phone(user.getPhone())
                    .completedToday((int) completedToday)
                    .assignedRooms(assignedRooms)
                    .build();
        }).collect(Collectors.toList());
    }
}
