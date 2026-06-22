package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.StoreIssueDTO;
import java.util.List;

public interface StoreIssueService {
    StandardResponse<StoreIssueDTO> createStoreIssue(StoreIssueDTO dto);
    StandardResponse<StoreIssueDTO> updateStoreIssue(Long id, StoreIssueDTO dto);
    StandardResponse<StoreIssueDTO> getStoreIssueById(Long id);
    StandardResponse<List<StoreIssueDTO>> getAllStoreIssues();
    StandardResponse<Void> deleteStoreIssue(Long id);
    StandardResponse<StoreIssueDTO> updateStatus(Long id, Long statusId);
}
