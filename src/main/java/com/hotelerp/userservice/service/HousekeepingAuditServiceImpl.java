package com.hotelerp.userservice.service;

import com.hotelerp.userservice.dto.CommonMasterDTO;
import com.hotelerp.userservice.dto.SOPCheckpointDTO;
import com.hotelerp.userservice.entity.CommonMaster;
import com.hotelerp.userservice.entity.SOPCheckpoint;
import com.hotelerp.userservice.entity.RoomAuditLog;
import com.hotelerp.userservice.repository.CommonMasterRepository;
import com.hotelerp.userservice.repository.SOPCheckpointRepository;
import com.hotelerp.userservice.repository.RoomAuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class HousekeepingAuditServiceImpl implements HousekeepingAuditService {

    private final CommonMasterRepository masterRepository;
    private final SOPCheckpointRepository checkpointRepository;
    private final RoomAuditLogRepository auditLogRepository;

    @Override
    public List<CommonMasterDTO> getMastersByCategory(String category) {
        return masterRepository.findByCategoryAndIsActiveTrue(category).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommonMasterDTO createMaster(CommonMasterDTO dto) {
        CommonMaster master = CommonMaster.builder()
                .category(dto.getCategory())
                .code(dto.getCode())
                .value(dto.getValue())
                .description(dto.getDescription())
                .isActive(true)
                .build();
        return convertToDTO(masterRepository.save(master));
    }

    @Override
    @Transactional
    public SOPCheckpointDTO createCheckpoint(SOPCheckpointDTO dto) {
        CommonMaster frequency = masterRepository.findById(dto.getFrequencyId())
                .orElseThrow(() -> new RuntimeException("Frequency not found"));
        CommonMaster responsibleRole = masterRepository.findById(dto.getResponsibleRoleId())
                .orElseThrow(() -> new RuntimeException("Responsible Role not found"));

        SOPCheckpoint checkpoint = SOPCheckpoint.builder()
                .checkpointId(dto.getCheckpointId())
                .frequency(frequency)
                .auditArea(dto.getAuditArea())
                .responsibleRole(responsibleRole)
                .description(dto.getDescription())
                .build();
        return convertToDTO(checkpointRepository.save(checkpoint));
    }

    @Override
    public List<SOPCheckpointDTO> getAllCheckpoints() {
        return checkpointRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SOPCheckpointDTO> getCheckpointsByFrequency(String frequency) {
        return checkpointRepository.findByFrequencyCode(frequency).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Object getRoomAuditStatus(Long roomId) {
        List<RoomAuditLog> logs = auditLogRepository.findLatestByRoomId(roomId);
        if (logs.isEmpty()) return null;
        
        // Return latest audit summary for the room
        RoomAuditLog latest = logs.get(0);
        Map<String, Object> status = new HashMap<>();
        status.put("roomId", roomId);
        status.put("lastAuditDate", latest.getAuditDate());
        status.put("overallStatus", latest.getStatus());
        status.put("score", latest.getScore());
        status.put("inspector", latest.getInspector() != null ? latest.getInspector().getFullName() : "System");
        return status;
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
