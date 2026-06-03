package com.hotelerp.userservice.controller;

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
    public ResponseEntity<MaintenanceDTO> reportIssue(@RequestBody MaintenanceDTO dto) {
        return new ResponseEntity<>(maintenanceService.reportIssue(dto), HttpStatus.CREATED);
    }

    @GetMapping(ServiceConstant.GET_MAINTENANCE_BY_ID)
    public ResponseEntity<MaintenanceDTO> getIssueById(@PathVariable Long id) {
        return ResponseEntity.ok(maintenanceService.getIssueById(id));
    }

    @GetMapping(ServiceConstant.GET_ALL_MAINTENANCE)
    public ResponseEntity<List<MaintenanceDTO>> getAllIssues() {
        return ResponseEntity.ok(maintenanceService.getAllIssues());
    }

    @PutMapping(ServiceConstant.UPDATE_MAINTENANCE)
    public ResponseEntity<MaintenanceDTO> updateIssue(@PathVariable Long id, @RequestBody MaintenanceDTO dto) {
        return ResponseEntity.ok(maintenanceService.updateIssue(id, dto));
    }

    @DeleteMapping(ServiceConstant.DELETE_MAINTENANCE)
    public ResponseEntity<Void> deleteIssue(@PathVariable Long id) {
        maintenanceService.deleteIssue(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(ServiceConstant.UPDATE_MAINTENANCE_STATUS)
    public ResponseEntity<MaintenanceDTO> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(maintenanceService.updateStatus(id, status));
    }
}
