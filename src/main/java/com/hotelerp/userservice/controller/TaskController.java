package com.hotelerp.userservice.controller;

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
    public ResponseEntity<Void> createTask(@RequestBody TaskDTO taskDTO) {
        taskService.createTask(taskDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping(ServiceConstant.GET_TASK_BY_ID)
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @GetMapping(ServiceConstant.GET_ALL_TASKS)
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @PutMapping(ServiceConstant.UPDATE_TASK)
    public ResponseEntity<TaskDTO> updateTask(@PathVariable Long id, @RequestBody TaskDTO taskDTO) {
        return ResponseEntity.ok(taskService.updateTask(id, taskDTO));
    }

    @DeleteMapping(ServiceConstant.DELETE_TASK)
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(ServiceConstant.UPDATE_TASK_STATUS)
    public ResponseEntity<TaskDTO> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(taskService.updateStatus(id, status));
    }
}
