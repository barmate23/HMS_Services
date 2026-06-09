package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.*;
import com.hotelerp.userservice.entity.*;
import com.hotelerp.userservice.repository.*;
import com.hotelerp.userservice.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class HousekeepingAuditServiceImpl implements HousekeepingAuditService {

    private final CommonMasterRepository masterRepository;
    private final SOPCheckpointRepository checkpointRepository;
    private final RoomAuditLogRepository auditLogRepository;
    private final RoomRepository roomRepository;

    @Override
    public StandardResponse<List<CommonMasterDTO>> getMastersByCategory(String category) {
        try {
            List<CommonMasterDTO> dtos = masterRepository.findByCategoryAndIsActiveTrue(category).stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return StandardResponse.success(dtos, "Master data fetched successfully for category: " + category);
        } catch (Exception e) {
            log.error("Error fetching master data by category: ", e);
            return StandardResponse.error("Failed to fetch master data", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    @Transactional
    public StandardResponse<Void> createMaster(CommonMasterDTO dto) {
        try {
            CommonMaster master = CommonMaster.builder()
                    .category(dto.getCategory())
                    .code(dto.getCode())
                    .value(dto.getValue())
                    .description(dto.getDescription())
                    .isActive(true)
                    .build();
            masterRepository.save(master);
            return StandardResponse.success("Common master data created successfully");
        } catch (Exception e) {
            log.error("Error creating common master: ", e);
            return StandardResponse.error("Failed to create master data", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    @Transactional
    public StandardResponse<Void> createCheckpoint(SOPCheckpointDTO dto) {
        try {
            CommonMaster frequency = masterRepository.findById(dto.getFrequencyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Frequency master data not found for ID: " + dto.getFrequencyId()));
            
            CommonMaster responsibleRole = masterRepository.findById(dto.getResponsibleRoleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Responsible Role master data not found for ID: " + dto.getResponsibleRoleId()));

            SOPCheckpoint checkpoint = SOPCheckpoint.builder()
                    .checkpointId(dto.getCheckpointId())
                    .frequency(frequency)
                    .auditArea(dto.getAuditArea())
                    .responsibleRole(responsibleRole)
                    .description(dto.getDescription())
                    .build();
            
            checkpointRepository.save(checkpoint);
            return StandardResponse.success("SOP checkpoint created successfully");
        } catch (ResourceNotFoundException e) {
            return StandardResponse.error(e.getMessage(), "RESOURCE_NOT_FOUND", e.getMessage());
        } catch (Exception e) {
            log.error("Error creating SOP checkpoint: ", e);
            return StandardResponse.error("Failed to create SOP checkpoint", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    public StandardResponse<List<SOPCheckpointDTO>> getAllCheckpoints() {
        try {
            List<SOPCheckpointDTO> dtos = checkpointRepository.findAll().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return StandardResponse.success(dtos, "SOP checkpoints fetched successfully");
        } catch (Exception e) {
            log.error("Error fetching all SOP checkpoints: ", e);
            return StandardResponse.error("Failed to fetch SOP checkpoints", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    public StandardResponse<List<SOPCheckpointDTO>> getCheckpointsByFrequency(String frequency) {
        try {
            List<SOPCheckpointDTO> dtos = checkpointRepository.findByFrequencyCode(frequency).stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return StandardResponse.success(dtos, "SOP checkpoints fetched successfully for frequency: " + frequency);
        } catch (Exception e) {
            log.error("Error fetching SOP checkpoints by frequency: ", e);
            return StandardResponse.error("Failed to fetch SOP checkpoints", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    public StandardResponse<Object> getRoomAuditStatus(Long roomId) {
        try {
            List<RoomAuditLog> logs = auditLogRepository.findLatestByRoomId(roomId);
            if (logs.isEmpty()) {
                return StandardResponse.success(null, "No audit history found for Room ID: " + roomId);
            }
            
            RoomAuditLog latest = logs.get(0);
            Map<String, Object> status = new HashMap<>();
            status.put("roomId", roomId);
            status.put("lastAuditDate", latest.getAuditDate());
            status.put("overallStatus", latest.getStatus());
            status.put("score", latest.getScore());
            status.put("inspector", latest.getInspector() != null ? latest.getInspector().getFullName() : "System");
            
            return StandardResponse.success(status, "Room audit status fetched successfully");
        } catch (Exception e) {
            log.error("Error fetching room audit status: ", e);
            return StandardResponse.error("Failed to fetch room audit status", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    public StandardResponse<List<RoomAuditStatusDTO>> getAuditStatusByFloorAndFrequency(Long floorId, String frequencyCode) {
        try {
            List<Room> rooms = roomRepository.findByFloorId(floorId);
            List<SOPCheckpoint> checkpoints = checkpointRepository.findByFrequencyCode(frequencyCode);
            
            List<RoomAuditStatusDTO> statusList = rooms.stream().map(room -> {
                List<RoomAuditLog> logs = auditLogRepository.findLatestByRoomId(room.getId());
                RoomAuditLog latestOverall = logs.isEmpty() ? null : logs.get(0);

                List<CheckpointStatusDTO> checkpointStatuses = checkpoints.stream().map(cp -> {
                    List<RoomAuditLog> cpLogs = auditLogRepository.findLatestByRoomAndCheckpoint(room.getId(), cp.getId());
                    String status = cpLogs.isEmpty() ? "PENDING" : cpLogs.get(0).getStatus();
                    
                    return CheckpointStatusDTO.builder()
                            .checkpointId(cp.getCheckpointId())
                            .auditArea(cp.getAuditArea())
                            .description(cp.getDescription())
                            .status(status)
                            .build();
                }).collect(Collectors.toList());

                return RoomAuditStatusDTO.builder()
                        .roomId(room.getId())
                        .roomNumber(room.getRoomNumber())
                        .roomType(room.getRoomType() != null ? room.getRoomType().getName() : "Unknown")
                        .pmsStatus(room.getStatus() != null ? room.getStatus().name() : "VACANT")
                        .hkStatus(latestOverall != null ? latestOverall.getStatus() : "UNCLEANED")
                        .inspectorName(latestOverall != null && latestOverall.getInspector() != null ? latestOverall.getInspector().getFullName() : "Unassigned")
                        .lastAuditDate(latestOverall != null ? latestOverall.getAuditDate() : null)
                        .overallScore(latestOverall != null ? latestOverall.getScore() : 0)
                        .checkpoints(checkpointStatuses)
                        .build();
            }).collect(Collectors.toList());

            return StandardResponse.success(statusList, "Audit status fetched successfully for floor and frequency");
        } catch (Exception e) {
            log.error("Error fetching audit status for floor and frequency: ", e);
            return StandardResponse.error("Failed to fetch audit status", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    private CommonMasterDTO convertToDTO(CommonMaster master) {
        CommonMasterDTO dto = new CommonMasterDTO();
        dto.setId(master.getId());
        dto.setCategory(master.getCategory());
        dto.setCode(master.getCode());
        dto.setValue(master.getValue());
        dto.setDescription(master.getDescription());
        return dto;
    }

    private SOPCheckpointDTO convertToDTO(SOPCheckpoint checkpoint) {
        SOPCheckpointDTO dto = new SOPCheckpointDTO();
        dto.setId(checkpoint.getId());
        dto.setCheckpointId(checkpoint.getCheckpointId());
        
        if (checkpoint.getFrequency() != null) {
            dto.setFrequencyId(checkpoint.getFrequency().getId());
            dto.setFrequencyValue(checkpoint.getFrequency().getValue());
        }
        
        dto.setAuditArea(checkpoint.getAuditArea());
        
        if (checkpoint.getResponsibleRole() != null) {
            dto.setResponsibleRoleId(checkpoint.getResponsibleRole().getId());
            dto.setResponsibleRoleValue(checkpoint.getResponsibleRole().getValue());
        }
        
        dto.setDescription(checkpoint.getDescription());
        return dto;
    }
}
