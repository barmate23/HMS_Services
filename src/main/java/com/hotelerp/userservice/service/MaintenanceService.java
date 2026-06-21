package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.MaintenanceDTO;
import java.util.List;

public interface MaintenanceService {
    StandardResponse<Void> reportIssue(MaintenanceDTO dto);
    StandardResponse<MaintenanceDTO> updateIssue(Long id, MaintenanceDTO dto);
    StandardResponse<MaintenanceDTO> getIssueById(Long id);
    StandardResponse<List<MaintenanceDTO>> getAllIssues();
    StandardResponse<List<MaintenanceDTO>> getActiveMaintenance();
    StandardResponse<Void> deleteIssue(Long id);
    StandardResponse<MaintenanceDTO> updateStatus(Long id, String status);
}
