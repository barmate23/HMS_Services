package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.TaskDTO;
import java.util.List;

public interface TaskService {
    StandardResponse<Void> createTask(TaskDTO taskDTO);
    StandardResponse<TaskDTO> updateTask(Long id, TaskDTO taskDTO);
    StandardResponse<TaskDTO> getTaskById(Long id);
    StandardResponse<List<TaskDTO>> getAllTasks();
    StandardResponse<List<TaskDTO>> getActiveTasks();
    StandardResponse<Void> deleteTask(Long id);
    StandardResponse<TaskDTO> updateStatus(Long id, String status);
}
