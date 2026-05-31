package com.hotelerp.userservice.service;

import com.hotelerp.userservice.dto.CommonMasterDTO;
import com.hotelerp.userservice.dto.SOPCheckpointDTO;
import java.util.List;

public interface HousekeepingAuditService {
    // Common Master
    List<CommonMasterDTO> getMastersByCategory(String category);
    CommonMasterDTO createMaster(CommonMasterDTO dto);

    // SOP Checkpoints
    SOPCheckpointDTO createCheckpoint(SOPCheckpointDTO dto);
    List<SOPCheckpointDTO> getAllCheckpoints();
    List<SOPCheckpointDTO> getCheckpointsByFrequency(String frequency);
    
    // Audit Status (Simplified for now)
    Object getRoomAuditStatus(Long roomId);
}
