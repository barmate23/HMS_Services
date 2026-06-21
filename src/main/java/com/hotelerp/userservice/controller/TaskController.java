package com.hotelerp.userservice.controller;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.TaskDTO;
import com.hotelerp.userservice.service.TaskService;
import com.hotelerp.userservice.constant.ServiceConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hmsService/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping(ServiceConstant.CREATE_TASK)
    public ResponseEntity<StandardResponse<Void>> createTask(@RequestBody TaskDTO taskDTO) {
        StandardResponse<Void> response = taskService.createTask(taskDTO);
        HttpStatus status = response.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping(ServiceConstant.GET_TASK_BY_ID)
    public ResponseEntity<StandardResponse<TaskDTO>> getTaskById(@PathVariable Long id) {
        StandardResponse<TaskDTO> response = taskService.getTaskById(id);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping(ServiceConstant.GET_ALL_TASKS)
    public ResponseEntity<StandardResponse<List<TaskDTO>>> getAllTasks() {
        StandardResponse<List<TaskDTO>> response = taskService.getAllTasks();
        return ResponseEntity.ok(response);
    }

    @GetMapping(ServiceConstant.GET_ACTIVE_TASKS)
    public ResponseEntity<StandardResponse<List<TaskDTO>>> getActiveTasks() {
        StandardResponse<List<TaskDTO>> response = taskService.getActiveTasks();
        return ResponseEntity.ok(response);
    }

    @PutMapping(ServiceConstant.UPDATE_TASK)
    public ResponseEntity<StandardResponse<TaskDTO>> updateTask(@PathVariable Long id, @RequestBody TaskDTO taskDTO) {
        StandardResponse<TaskDTO> response = taskService.updateTask(id, taskDTO);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @DeleteMapping(ServiceConstant.DELETE_TASK)
    public ResponseEntity<StandardResponse<Void>> deleteTask(@PathVariable Long id) {
        StandardResponse<Void> response = taskService.deleteTask(id);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @PatchMapping(ServiceConstant.UPDATE_TASK_STATUS)
    public ResponseEntity<StandardResponse<TaskDTO>> updateStatus(@PathVariable Long id, @RequestParam String status) {
        StandardResponse<TaskDTO> response = taskService.updateStatus(id, status);
        HttpStatus httpStatus = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(httpStatus).body(response);
    }
}
