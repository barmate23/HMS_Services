package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.CommonMasterDTO;
import com.hotelerp.userservice.dto.SOPCheckpointDTO;
import java.util.List;

public interface HousekeepingAuditService {
    // Common Master
    StandardResponse<List<CommonMasterDTO>> getMastersByCategory(String category);
    StandardResponse<Void> createMaster(CommonMasterDTO dto);

    // SOP Checkpoints
    StandardResponse<Void> createCheckpoint(SOPCheckpointDTO dto);
    StandardResponse<List<SOPCheckpointDTO>> getAllCheckpoints();
    StandardResponse<List<SOPCheckpointDTO>> getCheckpointsByFrequency(String frequency);
    
    // Audit Status (Simplified for now)
    StandardResponse<Object> getRoomAuditStatus(Long roomId);
}
