package com.hotelerp.userservice.service;

import com.hotelerp.userservice.dto.MaintenanceDTO;
import java.util.List;

public interface MaintenanceService {
    MaintenanceDTO reportIssue(MaintenanceDTO dto);
    MaintenanceDTO updateIssue(Long id, MaintenanceDTO dto);
    MaintenanceDTO getIssueById(Long id);
    List<MaintenanceDTO> getAllIssues();
    void deleteIssue(Long id);
    MaintenanceDTO updateStatus(Long id, String status);
}
