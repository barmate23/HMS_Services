package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.StoreIssueDTO;
import com.hotelerp.userservice.entity.*;
import com.hotelerp.userservice.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreIssueServiceImpl implements StoreIssueService {

    private final StoreIssueRepository storeIssueRepository;
    private final CommonMasterRepository commonMasterRepository;
    private final InventoryStockRepository inventoryStockRepository;

    private String generateIssueNumber() {
        String ts = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
        return "ISS-" + ts.substring(4, 10);
    }

    @Override
    @Transactional
    public StandardResponse<StoreIssueDTO> createStoreIssue(StoreIssueDTO dto) {
        try {
            InventoryStock item = inventoryStockRepository.findByIdAndIsDeletedFalse(dto.getItemId())
                    .orElseThrow(() -> new RuntimeException("Item not found"));

            StoreIssue issue = StoreIssue.builder()
                    .issueNumber(generateIssueNumber())
                    .issuedTo(dto.getIssuedTo())
                    .item(item)
                    .quantity(dto.getQuantity())
                    .issueNote(dto.getIssueNote())
                    .issueDate(dto.getIssueDate())
                    .build();

            if (dto.getDepartmentId() != null) {
                issue.setDepartment(commonMasterRepository.findById(dto.getDepartmentId())
                        .orElseThrow(() -> new RuntimeException("Department not found")));
            }

            if (dto.getStatusId() != null) {
                issue.setStatus(commonMasterRepository.findById(dto.getStatusId())
                        .orElseThrow(() -> new RuntimeException("Status not found")));
            } else {
                // Default to ISSUED
                commonMasterRepository.findByCategoryAndCode("STOCK_ISSUE_STATUS", "ISSUED")
                        .ifPresent(issue::setStatus);
            }

            // Deduct stock on-hand quantity
            if (dto.getQuantity() != null && item.getOnHand() != null) {
                item.setOnHand(item.getOnHand().subtract(java.math.BigDecimal.valueOf(dto.getQuantity())));
                inventoryStockRepository.save(item);
            }

            issue = storeIssueRepository.save(issue);
            return StandardResponse.success(convertToDTO(issue), "Store issue created successfully");
        } catch (Exception e) {
            log.error("Error creating store issue: ", e);
            return StandardResponse.error("Failed to create store issue", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    @Transactional
    public StandardResponse<StoreIssueDTO> updateStoreIssue(Long id, StoreIssueDTO dto) {
        try {
            StoreIssue issue = storeIssueRepository.findByIdAndIsDeletedFalse(id)
                    .orElseThrow(() -> new RuntimeException("Store issue not found"));

            issue.setIssuedTo(dto.getIssuedTo());
            issue.setQuantity(dto.getQuantity());
            issue.setIssueNote(dto.getIssueNote());
            issue.setIssueDate(dto.getIssueDate());

            if (dto.getDepartmentId() != null) {
                issue.setDepartment(commonMasterRepository.findById(dto.getDepartmentId())
                        .orElseThrow(() -> new RuntimeException("Department not found")));
            }

            if (dto.getItemId() != null) {
                InventoryStock stock = inventoryStockRepository.findByIdAndIsDeletedFalse(dto.getItemId())
                        .orElseThrow(() -> new RuntimeException("Item not found"));
                issue.setItem(stock);
            }

            if (dto.getStatusId() != null) {
                issue.setStatus(commonMasterRepository.findById(dto.getStatusId())
                        .orElseThrow(() -> new RuntimeException("Status not found")));
            }

            issue = storeIssueRepository.save(issue);
            return StandardResponse.success(convertToDTO(issue), "Store issue updated successfully");
        } catch (Exception e) {
            log.error("Error updating store issue: ", e);
            return StandardResponse.error("Failed to update store issue", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    public StandardResponse<StoreIssueDTO> getStoreIssueById(Long id) {
        try {
            StoreIssue issue = storeIssueRepository.findByIdAndIsDeletedFalse(id)
                    .orElseThrow(() -> new RuntimeException("Store issue not found"));
            return StandardResponse.success(convertToDTO(issue), "Store issue fetched");
        } catch (Exception e) {
            return StandardResponse.error("Not found", "NOT_FOUND", e.getMessage());
        }
    }

    @Override
    public StandardResponse<List<StoreIssueDTO>> getAllStoreIssues() {
        try {
            List<StoreIssueDTO> list = storeIssueRepository.findByIsDeletedFalse().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return StandardResponse.success(list, "Store issues fetched");
        } catch (Exception e) {
            return StandardResponse.error("Failed to fetch", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    @Transactional
    public StandardResponse<Void> deleteStoreIssue(Long id) {
        try {
            StoreIssue issue = storeIssueRepository.findByIdAndIsDeletedFalse(id)
                    .orElseThrow(() -> new RuntimeException("Store issue not found"));
            issue.setIsDeleted(true);
            storeIssueRepository.save(issue);
            return StandardResponse.success("Store issue deleted");
        } catch (Exception e) {
            return StandardResponse.error("Failed to delete", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    @Transactional
    public StandardResponse<StoreIssueDTO> updateStatus(Long id, Long statusId) {
        try {
            StoreIssue issue = storeIssueRepository.findByIdAndIsDeletedFalse(id)
                    .orElseThrow(() -> new RuntimeException("Store issue not found"));
            CommonMaster status = commonMasterRepository.findById(statusId)
                    .orElseThrow(() -> new RuntimeException("Status not found"));
            issue.setStatus(status);
            issue = storeIssueRepository.save(issue);
            return StandardResponse.success(convertToDTO(issue), "Status updated successfully");
        } catch (Exception e) {
            log.error("Error updating store issue status: ", e);
            return StandardResponse.error("Failed to update status", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    private StoreIssueDTO convertToDTO(StoreIssue i) {
        return StoreIssueDTO.builder()
                .id(i.getId())
                .issueNumber(i.getIssueNumber())
                .departmentId(i.getDepartment() != null ? i.getDepartment().getId() : null)
                .departmentName(i.getDepartment() != null ? i.getDepartment().getValue() : null)
                .issuedTo(i.getIssuedTo())
                .itemId(i.getItem().getId())
                .itemName(i.getItem().getItemName())
                .itemCode(i.getItem().getItemCode())
                .quantity(i.getQuantity())
                .issueNote(i.getIssueNote())
                .issueDate(i.getIssueDate())
                .statusId(i.getStatus() != null ? i.getStatus().getId() : null)
                .statusName(i.getStatus() != null ? i.getStatus().getValue() : null)
                .statusCode(i.getStatus() != null ? i.getStatus().getCode() : null)
                .createdAt(i.getCreatedAt())
                .updatedAt(i.getUpdatedAt())
                .build();
    }
}
