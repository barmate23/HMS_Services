package com.hotelerp.userservice.controller;

import com.hotelerp.userservice.dto.CommonMasterDTO;
import com.hotelerp.userservice.dto.SOPCheckpointDTO;
import com.hotelerp.userservice.service.HousekeepingAuditService;
import com.hotelerp.userservice.constant.ServiceConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/housekeeping/audit")
@RequiredArgsConstructor
public class HousekeepingAuditController {

    private final HousekeepingAuditService auditService;

    // Dropdown/Master Data Endpoints
    @GetMapping(ServiceConstant.GET_COMMON_MASTER)
    public ResponseEntity<List<CommonMasterDTO>> getMastersByCategory(@PathVariable String category) {
        return ResponseEntity.ok(auditService.getMastersByCategory(category));
    }

    // SOP Checkpoint Endpoints
    @PostMapping(ServiceConstant.CREATE_CHECKPOINT)
    public ResponseEntity<SOPCheckpointDTO> createCheckpoint(@RequestBody SOPCheckpointDTO dto) {
        return new ResponseEntity<>(auditService.createCheckpoint(dto), HttpStatus.CREATED);
    }

    @GetMapping(ServiceConstant.GET_ALL_CHECKPOINTS)
    public ResponseEntity<List<SOPCheckpointDTO>> getAllCheckpoints() {
        return ResponseEntity.ok(auditService.getAllCheckpoints());
    }

    @GetMapping(ServiceConstant.GET_CHECKPOINTS_BY_FREQUENCY)
    public ResponseEntity<List<SOPCheckpointDTO>> getCheckpointsByFrequency(@PathVariable String frequency) {
        return ResponseEntity.ok(auditService.getCheckpointsByFrequency(frequency));
    }

    // Live Room Audit Status Endpoint
    // @GetMapping(ServiceConstant.GET_ROOM_LIVE_STATUS)
    // public ResponseEntity<Object> getRoomAuditStatus(@PathVariable Long roomId) {
    // Object status = auditService.getRoomAuditStatus(roomId);
    // if (status == null) {
    // return ResponseEntity.notFound().build();
    // }
    // return ResponseEntity.ok(status);
    // }
}
