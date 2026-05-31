package com.hotelerp.userservice.service;

import com.hotelerp.userservice.dto.TaskDTO;
import java.util.List;

public interface TaskService {
    TaskDTO createTask(TaskDTO taskDTO);
    TaskDTO updateTask(Long id, TaskDTO taskDTO);
    TaskDTO getTaskById(Long id);
    List<TaskDTO> getAllTasks();
    void deleteTask(Long id);
    TaskDTO updateStatus(Long id, String status);
}
