package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.PurchaseOrderDTO;
import com.hotelerp.userservice.entity.*;
import com.hotelerp.userservice.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SupplierRepository supplierRepository;
    private final CommonMasterRepository commonMasterRepository;

    @Override
    @Transactional
    public StandardResponse<PurchaseOrderDTO> createPurchaseOrder(PurchaseOrderDTO dto) {
        try {
            Supplier supplier = supplierRepository.findByIdAndIsDeletedFalse(dto.getSupplierId())
                    .orElseThrow(() -> new RuntimeException("Supplier not found"));

            PurchaseOrder po = PurchaseOrder.builder()
                    .poNumber(dto.getPoNumber())
                    .supplier(supplier)
                    .expectedDate(dto.getExpectedDate())
                    .itemCount(dto.getItemCount())
                    .poNote(dto.getPoNote())
                    .totalAmount(dto.getTotalAmount())
                    .build();

            if (dto.getDepartmentId() != null) {
                po.setDepartment(commonMasterRepository.findById(dto.getDepartmentId())
                        .orElseThrow(() -> new RuntimeException("Department not found")));
            }

            if (dto.getStatusId() != null) {
                po.setStatus(commonMasterRepository.findById(dto.getStatusId())
                        .orElseThrow(() -> new RuntimeException("Status not found")));
            } else {
                commonMasterRepository.findByCategoryAndCode("PO_STATUS", "DRAFT")
                        .ifPresent(po::setStatus);
            }

            po = purchaseOrderRepository.save(po);
            return StandardResponse.success(convertToDTO(po), "Purchase Order created successfully");
        } catch (Exception e) {
            log.error("Error creating PO: ", e);
            return StandardResponse.error("Failed to create PO", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    @Transactional
    public StandardResponse<PurchaseOrderDTO> updatePurchaseOrder(Long id, PurchaseOrderDTO dto) {
        try {
            PurchaseOrder po = purchaseOrderRepository.findByIdAndIsDeletedFalse(id)
                    .orElseThrow(() -> new RuntimeException("Purchase Order not found"));

            po.setPoNumber(dto.getPoNumber());
            po.setExpectedDate(dto.getExpectedDate());
            po.setItemCount(dto.getItemCount());
            po.setPoNote(dto.getPoNote());
            po.setTotalAmount(dto.getTotalAmount());

            if (dto.getSupplierId() != null) {
                po.setSupplier(supplierRepository.findByIdAndIsDeletedFalse(dto.getSupplierId())
                        .orElseThrow(() -> new RuntimeException("Supplier not found")));
            }

            if (dto.getDepartmentId() != null) {
                po.setDepartment(commonMasterRepository.findById(dto.getDepartmentId())
                        .orElseThrow(() -> new RuntimeException("Department not found")));
            }

            if (dto.getStatusId() != null) {
                po.setStatus(commonMasterRepository.findById(dto.getStatusId())
                        .orElseThrow(() -> new RuntimeException("Status not found")));
            }

            po = purchaseOrderRepository.save(po);
            return StandardResponse.success(convertToDTO(po), "Purchase Order updated successfully");
        } catch (Exception e) {
            log.error("Error updating PO: ", e);
            return StandardResponse.error("Failed to update PO", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    public StandardResponse<PurchaseOrderDTO> getPurchaseOrderById(Long id) {
        try {
            PurchaseOrder po = purchaseOrderRepository.findByIdAndIsDeletedFalse(id)
                    .orElseThrow(() -> new RuntimeException("Purchase Order not found"));
            return StandardResponse.success(convertToDTO(po), "PO fetched successfully");
        } catch (Exception e) {
            return StandardResponse.error("Not found", "NOT_FOUND", e.getMessage());
        }
    }

    @Override
    public StandardResponse<List<PurchaseOrderDTO>> getAllPurchaseOrders() {
        try {
            List<PurchaseOrderDTO> list = purchaseOrderRepository.findByIsDeletedFalse().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return StandardResponse.success(list, "Purchase Orders fetched successfully");
        } catch (Exception e) {
            return StandardResponse.error("Failed to fetch POs", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    @Transactional
    public StandardResponse<Void> deletePurchaseOrder(Long id) {
        try {
            PurchaseOrder po = purchaseOrderRepository.findByIdAndIsDeletedFalse(id)
                    .orElseThrow(() -> new RuntimeException("Purchase Order not found"));
            po.setIsDeleted(true);
            purchaseOrderRepository.save(po);
            return StandardResponse.success("PO deleted successfully");
        } catch (Exception e) {
            return StandardResponse.error("Failed to delete PO", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    @Transactional
    public StandardResponse<PurchaseOrderDTO> updateStatus(Long id, Long statusId) {
        try {
            PurchaseOrder po = purchaseOrderRepository.findByIdAndIsDeletedFalse(id)
                    .orElseThrow(() -> new RuntimeException("Purchase Order not found"));
            CommonMaster status = commonMasterRepository.findById(statusId)
                    .orElseThrow(() -> new RuntimeException("Status not found"));
            po.setStatus(status);
            po = purchaseOrderRepository.save(po);
            return StandardResponse.success(convertToDTO(po), "PO status updated successfully");
        } catch (Exception e) {
            return StandardResponse.error("Failed to update status", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    private PurchaseOrderDTO convertToDTO(PurchaseOrder po) {
        return PurchaseOrderDTO.builder()
                .id(po.getId())
                .poNumber(po.getPoNumber())
                .supplierId(po.getSupplier().getId())
                .supplierName(po.getSupplier().getSupplierName())
                .departmentId(po.getDepartment() != null ? po.getDepartment().getId() : null)
                .departmentName(po.getDepartment() != null ? po.getDepartment().getValue() : null)
                .expectedDate(po.getExpectedDate())
                .itemCount(po.getItemCount())
                .poNote(po.getPoNote())
                .totalAmount(po.getTotalAmount())
                .statusId(po.getStatus() != null ? po.getStatus().getId() : null)
                .statusName(po.getStatus() != null ? po.getStatus().getValue() : null)
                .statusCode(po.getStatus() != null ? po.getStatus().getCode() : null)
                .createdAt(po.getCreatedAt())
                .updatedAt(po.getUpdatedAt())
                .build();
    }
}
