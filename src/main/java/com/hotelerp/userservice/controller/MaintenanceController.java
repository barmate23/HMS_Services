package com.hotelerp.userservice.controller;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.MaintenanceDTO;
import com.hotelerp.userservice.service.MaintenanceService;
import com.hotelerp.userservice.constant.ServiceConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hmsService/v1/maintenance")
@RequiredArgsConstructor
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    @PostMapping(ServiceConstant.CREATE_MAINTENANCE)
    public ResponseEntity<StandardResponse<Void>> reportIssue(@RequestBody MaintenanceDTO dto) {
        StandardResponse<Void> response = maintenanceService.reportIssue(dto);
        HttpStatus status = response.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping(ServiceConstant.GET_MAINTENANCE_BY_ID)
    public ResponseEntity<StandardResponse<MaintenanceDTO>> getIssueById(@PathVariable Long id) {
        StandardResponse<MaintenanceDTO> response = maintenanceService.getIssueById(id);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping(ServiceConstant.GET_ALL_MAINTENANCE)
    public ResponseEntity<StandardResponse<List<MaintenanceDTO>>> getAllIssues() {
        StandardResponse<List<MaintenanceDTO>> response = maintenanceService.getAllIssues();
        return ResponseEntity.ok(response);
    }

    @PutMapping(ServiceConstant.UPDATE_MAINTENANCE)
    public ResponseEntity<StandardResponse<MaintenanceDTO>> updateIssue(@PathVariable Long id, @RequestBody MaintenanceDTO dto) {
        StandardResponse<MaintenanceDTO> response = maintenanceService.updateIssue(id, dto);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @DeleteMapping(ServiceConstant.DELETE_MAINTENANCE)
    public ResponseEntity<StandardResponse<Void>> deleteIssue(@PathVariable Long id) {
        StandardResponse<Void> response = maintenanceService.deleteIssue(id);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @PatchMapping(ServiceConstant.UPDATE_MAINTENANCE_STATUS)
    public ResponseEntity<StandardResponse<MaintenanceDTO>> updateStatus(@PathVariable Long id, @RequestParam String status) {
        StandardResponse<MaintenanceDTO> response = maintenanceService.updateStatus(id, status);
        HttpStatus httpStatus = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(httpStatus).body(response);
    }
}
