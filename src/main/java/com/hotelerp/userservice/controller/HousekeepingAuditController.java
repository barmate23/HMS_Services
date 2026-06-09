package com.hotelerp.userservice.controller;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.CommonMasterDTO;
import com.hotelerp.userservice.dto.SOPCheckpointDTO;
import com.hotelerp.userservice.dto.RoomAuditStatusDTO;
import com.hotelerp.userservice.dto.RoomAuditSaveRequest;

import com.hotelerp.userservice.service.HousekeepingAuditService;
import com.hotelerp.userservice.constant.ServiceConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hmsService/v1/housekeeping/audit")
@RequiredArgsConstructor
public class HousekeepingAuditController {

    private final HousekeepingAuditService auditService;

    // Dropdown/Master Data Endpoints
    @GetMapping(ServiceConstant.GET_COMMON_MASTER)
    public ResponseEntity<StandardResponse<List<CommonMasterDTO>>> getMastersByCategory(@PathVariable String category) {
        StandardResponse<List<CommonMasterDTO>> response = auditService.getMastersByCategory(category);
        return ResponseEntity.ok(response);
    }

    // SOP Checkpoint Endpoints
    @PostMapping(ServiceConstant.CREATE_CHECKPOINT)
    public ResponseEntity<StandardResponse<Void>> createCheckpoint(@RequestBody SOPCheckpointDTO dto) {
        StandardResponse<Void> response = auditService.createCheckpoint(dto);
        HttpStatus status = response.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping(ServiceConstant.GET_ALL_CHECKPOINTS)
    public ResponseEntity<StandardResponse<List<SOPCheckpointDTO>>> getAllCheckpoints() {
        StandardResponse<List<SOPCheckpointDTO>> response = auditService.getAllCheckpoints();
        return ResponseEntity.ok(response);
    }

    @GetMapping(ServiceConstant.GET_CHECKPOINTS_BY_FREQUENCY)
    public ResponseEntity<StandardResponse<List<SOPCheckpointDTO>>> getCheckpointsByFrequency(
            @PathVariable String frequency) {
        StandardResponse<List<SOPCheckpointDTO>> response = auditService.getCheckpointsByFrequency(frequency);
        return ResponseEntity.ok(response);
    }

    // Live Room Audit Status Endpoint
    @GetMapping(ServiceConstant.GET_ROOM_LIVE_STATUS)
    public ResponseEntity<StandardResponse<Object>> getRoomAuditStatus(@PathVariable Long roomId) {
        StandardResponse<Object> response = auditService.getRoomAuditStatus(roomId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getRoomAuditStatus")
    public ResponseEntity<StandardResponse<List<RoomAuditStatusDTO>>> getAuditStatusByFloorAndFrequency(
            @RequestParam Long floorId,
            @RequestParam String frequency) {
        StandardResponse<List<RoomAuditStatusDTO>> response = auditService.getAuditStatusByFloorAndFrequency(floorId,
                frequency);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/saveRoomAudit")
    public ResponseEntity<StandardResponse<Void>> saveRoomAudit(@RequestBody RoomAuditSaveRequest request) {
        StandardResponse<Void> response = auditService.saveRoomAudit(request);
        return ResponseEntity.ok(response);
    }
}

