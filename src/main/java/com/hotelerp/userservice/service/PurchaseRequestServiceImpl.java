package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.PurchaseRequestDTO;
import com.hotelerp.userservice.entity.*;
import com.hotelerp.userservice.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseRequestServiceImpl implements PurchaseRequestService {

    private final PurchaseRequestRepository purchaseRequestRepository;
    private final CommonMasterRepository commonMasterRepository;
    private final ItemConfigRepository itemConfigRepository;

    private String generatePrNumber() {
        String ts = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
        return "PR-" + ts.substring(4, 10);
    }

    @Override
    @Transactional
    public StandardResponse<PurchaseRequestDTO> createPurchaseRequest(PurchaseRequestDTO dto) {
        try {
            PurchaseRequest pr = PurchaseRequest.builder()
                    .prNumber(dto.getPrNumber())
                    .requestedBy(dto.getRequestedBy())
                    .neededBy(dto.getNeededBy())
                    .expectedAmount(dto.getExpectedAmount())
                    .justification(dto.getJustification())
                    .build();

            if (dto.getDepartmentId() != null) {
                pr.setDepartment(commonMasterRepository.findById(dto.getDepartmentId())
                        .orElseThrow(() -> new RuntimeException("Department not found")));
            }

            if (dto.getStatusId() != null) {
                pr.setStatus(commonMasterRepository.findById(dto.getStatusId())
                        .orElseThrow(() -> new RuntimeException("Status not found")));
            } else {
                // Default to DRAFT
                commonMasterRepository.findByCategoryAndCode("PR_STATUS", "DRAFT")
                        .ifPresent(pr::setStatus);
            }

            pr = purchaseRequestRepository.save(pr);

            if (dto.getItems() != null && !dto.getItems().isEmpty()) {
                List<PurchaseRequestItem> items = new ArrayList<>();
                for (PurchaseRequestDTO.PurchaseRequestItemDTO itemDTO : dto.getItems()) {
                    ItemConfig config = itemConfigRepository.findById(itemDTO.getItemId())
                            .orElseThrow(() -> new RuntimeException("Item not found: " + itemDTO.getItemId()));
                    items.add(PurchaseRequestItem.builder()
                            .purchaseRequest(pr)
                            .item(config)
                            .requiredQuantity(itemDTO.getRequiredQuantity())
                            .unitPrice(itemDTO.getUnitPrice())
                            .build());
                }
                pr.setItems(items);
                pr = purchaseRequestRepository.save(pr);
            }

            return StandardResponse.success(convertToDTO(pr), "Purchase request created successfully");
        } catch (Exception e) {
            log.error("Error creating purchase request: ", e);
            return StandardResponse.error("Failed to create purchase request", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    @Transactional
    public StandardResponse<PurchaseRequestDTO> updatePurchaseRequest(Long id, PurchaseRequestDTO dto) {
        try {
            PurchaseRequest pr = purchaseRequestRepository.findByIdAndIsDeletedFalse(id)
                    .orElseThrow(() -> new RuntimeException("Purchase request not found"));

            pr.setRequestedBy(dto.getRequestedBy());
            pr.setNeededBy(dto.getNeededBy());
            pr.setExpectedAmount(dto.getExpectedAmount());
            pr.setJustification(dto.getJustification());

            if (dto.getDepartmentId() != null) {
                pr.setDepartment(commonMasterRepository.findById(dto.getDepartmentId())
                        .orElseThrow(() -> new RuntimeException("Department not found")));
            }

            if (dto.getStatusId() != null) {
                pr.setStatus(commonMasterRepository.findById(dto.getStatusId())
                        .orElseThrow(() -> new RuntimeException("Status not found")));
            }

            if (dto.getItems() != null) {
                pr.getItems().clear();
                for (PurchaseRequestDTO.PurchaseRequestItemDTO itemDTO : dto.getItems()) {
                    ItemConfig config = itemConfigRepository.findById(itemDTO.getItemId())
                            .orElseThrow(() -> new RuntimeException("Item not found: " + itemDTO.getItemId()));
                    pr.getItems().add(PurchaseRequestItem.builder()
                            .purchaseRequest(pr)
                            .item(config)
                            .requiredQuantity(itemDTO.getRequiredQuantity())
                            .unitPrice(itemDTO.getUnitPrice())
                            .build());
                }
            }

            pr = purchaseRequestRepository.save(pr);
            return StandardResponse.success(convertToDTO(pr), "Purchase request updated successfully");
        } catch (Exception e) {
            log.error("Error updating purchase request: ", e);
            return StandardResponse.error("Failed to update purchase request", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    public StandardResponse<PurchaseRequestDTO> getPurchaseRequestById(Long id) {
        try {
            PurchaseRequest pr = purchaseRequestRepository.findByIdAndIsDeletedFalse(id)
                    .orElseThrow(() -> new RuntimeException("Purchase request not found"));
            return StandardResponse.success(convertToDTO(pr), "Purchase request fetched");
        } catch (Exception e) {
            return StandardResponse.error("Not found", "NOT_FOUND", e.getMessage());
        }
    }

    @Override
    public StandardResponse<List<PurchaseRequestDTO>> getAllPurchaseRequests() {
        try {
            List<PurchaseRequestDTO> list = purchaseRequestRepository.findByIsDeletedFalse().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return StandardResponse.success(list, "Purchase requests fetched");
        } catch (Exception e) {
            return StandardResponse.error("Failed to fetch", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    @Transactional
    public StandardResponse<Void> deletePurchaseRequest(Long id) {
        try {
            PurchaseRequest pr = purchaseRequestRepository.findByIdAndIsDeletedFalse(id)
                    .orElseThrow(() -> new RuntimeException("Purchase request not found"));
            pr.setIsDeleted(true);
            purchaseRequestRepository.save(pr);
            return StandardResponse.success("Purchase request deleted");
        } catch (Exception e) {
            return StandardResponse.error("Failed to delete", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    @Transactional
    public StandardResponse<PurchaseRequestDTO> updateStatus(Long id, Long statusId) {
        try {
            PurchaseRequest pr = purchaseRequestRepository.findByIdAndIsDeletedFalse(id)
                    .orElseThrow(() -> new RuntimeException("Purchase request not found"));
            CommonMaster status = commonMasterRepository.findById(statusId)
                    .orElseThrow(() -> new RuntimeException("Status not found"));
            pr.setStatus(status);
            pr = purchaseRequestRepository.save(pr);
            return StandardResponse.success(convertToDTO(pr), "Status updated successfully");
        } catch (Exception e) {
            log.error("Error updating PR status: ", e);
            return StandardResponse.error("Failed to update status", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    private PurchaseRequestDTO convertToDTO(PurchaseRequest pr) {
        List<PurchaseRequestDTO.PurchaseRequestItemDTO> itemDTOs = pr.getItems().stream()
                .map(i -> PurchaseRequestDTO.PurchaseRequestItemDTO.builder()
                        .id(i.getId())
                        .itemId(i.getItem().getId())
                        .itemName(i.getItem().getItemName())
                        .itemCode(i.getItem().getItemCode())
                        .requiredQuantity(i.getRequiredQuantity())
                        .unitPrice(i.getUnitPrice())
                        .build())
                .collect(Collectors.toList());

        return PurchaseRequestDTO.builder()
                .id(pr.getId())
                .prNumber(pr.getPrNumber())
                .departmentId(pr.getDepartment() != null ? pr.getDepartment().getId() : null)
                .departmentName(pr.getDepartment() != null ? pr.getDepartment().getValue() : null)
                .requestedBy(pr.getRequestedBy())
                .neededBy(pr.getNeededBy())
                .expectedAmount(pr.getExpectedAmount())
                .justification(pr.getJustification())
                .statusId(pr.getStatus() != null ? pr.getStatus().getId() : null)
                .statusName(pr.getStatus() != null ? pr.getStatus().getValue() : null)
                .statusCode(pr.getStatus() != null ? pr.getStatus().getCode() : null)
                .items(itemDTOs)
                .createdAt(pr.getCreatedAt())
                .updatedAt(pr.getUpdatedAt())
                .build();
    }
}
