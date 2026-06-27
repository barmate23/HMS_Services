package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.PurchaseOrderDTO;
import com.hotelerp.userservice.entity.*;
import com.hotelerp.userservice.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SupplierRepository supplierRepository;
    private final CommonMasterRepository commonMasterRepository;
    private final DepartmentRepository departmentRepository;
    private final PurchaseRequestRepository purchaseRequestRepository;
    private final ItemConfigRepository itemConfigRepository;
    private final PurchaseOrderLineRepository purchaseOrderLineRepository;

    @Override
    @Transactional
    public StandardResponse<PurchaseOrderDTO> createPurchaseOrder(PurchaseOrderDTO dto) {
        try {
            Supplier supplier = supplierRepository.findByIdAndIsDeletedFalse(dto.getSupplierId())
                    .orElseThrow(() -> new RuntimeException("Supplier not found"));

            PurchaseOrder po = PurchaseOrder.builder()
                    .poNumber(dto.getPoNumber())
                    .poDate(dto.getPoDate())
                    .supplier(supplier)
                    .expectedDate(dto.getExpectedDate())
                    .itemCount(dto.getItemCount())
                    .poNote(dto.getPoNote())
                    .totalAmount(dto.getTotalAmount())
                    .requestedBy(dto.getRequestedBy())
                    .build();

            if (dto.getDepartmentId() != null) {
                po.setDepartment(departmentRepository.findById(dto.getDepartmentId())
                        .orElseThrow(() -> new RuntimeException("Department not found")));
            }

            if (dto.getDeliveryStoreId() != null) {
                po.setDeliveryStore(commonMasterRepository.findById(dto.getDeliveryStoreId())
                        .orElseThrow(() -> new RuntimeException("Delivery Store not found")));
            }

            if (dto.getPaymentTermsId() != null) {
                po.setPaymentTerms(commonMasterRepository.findById(dto.getPaymentTermsId())
                        .orElseThrow(() -> new RuntimeException("Payment Terms not found")));
            }

            if (dto.getStatusId() != null) {
                po.setStatus(commonMasterRepository.findById(dto.getStatusId())
                        .orElseThrow(() -> new RuntimeException("Status not found")));
            } else {
                commonMasterRepository.findByCategoryAndCode("PO_STATUS", "DRAFT")
                        .ifPresent(po::setStatus);
            }

            po = purchaseOrderRepository.save(po);

            if (dto.getLines() != null && !dto.getLines().isEmpty()) {
                List<PurchaseOrderLine> lines = new ArrayList<>();
                for (PurchaseOrderDTO.PurchaseOrderLineDTO lineDTO : dto.getLines()) {
                    ItemConfig item = itemConfigRepository.findById(lineDTO.getItemId())
                            .orElseThrow(() -> new RuntimeException("Item not found: " + lineDTO.getItemId()));
                    lines.add(PurchaseOrderLine.builder()
                            .purchaseOrder(po)
                            .item(item)
                            .quantity(lineDTO.getQuantity())
                            .rate(lineDTO.getRate())
                            .discountPercentage(lineDTO.getDiscountPercentage())
                            .gstPercentage(lineDTO.getGstPercentage())
                            .totalAmount(lineDTO.getTotalAmount())
                            .build());
                }
                po.setLines(lines);
                po = purchaseOrderRepository.save(po);
            }

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
            po.setPoDate(dto.getPoDate());
            po.setExpectedDate(dto.getExpectedDate());
            po.setItemCount(dto.getItemCount());
            po.setPoNote(dto.getPoNote());
            po.setTotalAmount(dto.getTotalAmount());
            po.setRequestedBy(dto.getRequestedBy());

            if (dto.getSupplierId() != null) {
                po.setSupplier(supplierRepository.findByIdAndIsDeletedFalse(dto.getSupplierId())
                        .orElseThrow(() -> new RuntimeException("Supplier not found")));
            }

            if (dto.getPrId() != null) {
                po.setPurchaseRequest(dto.getPrId()
                        .stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(",")));
            }

            if (dto.getDeliveryStoreId() != null) {
                po.setDeliveryStore(commonMasterRepository.findById(dto.getDeliveryStoreId())
                        .orElseThrow(() -> new RuntimeException("Delivery Store not found")));
            }

            if (dto.getPaymentTermsId() != null) {
                po.setPaymentTerms(commonMasterRepository.findById(dto.getPaymentTermsId())
                        .orElseThrow(() -> new RuntimeException("Payment Terms not found")));
            }

            if (dto.getDepartmentId() != null) {
                po.setDepartment(departmentRepository.findById(dto.getDepartmentId())
                        .orElseThrow(() -> new RuntimeException("Department not found")));
            }

            if (dto.getStatusId() != null) {
                po.setStatus(commonMasterRepository.findById(dto.getStatusId())
                        .orElseThrow(() -> new RuntimeException("Status not found")));
            }

            if (dto.getLines() != null) {
                po.getLines().clear();
                for (PurchaseOrderDTO.PurchaseOrderLineDTO lineDTO : dto.getLines()) {
                    ItemConfig item = itemConfigRepository.findById(lineDTO.getItemId())
                            .orElseThrow(() -> new RuntimeException("Item not found: " + lineDTO.getItemId()));
                    po.getLines().add(PurchaseOrderLine.builder()
                            .purchaseOrder(po)
                            .item(item)
                            .quantity(lineDTO.getQuantity())
                            .rate(lineDTO.getRate())
                            .discountPercentage(lineDTO.getDiscountPercentage())
                            .gstPercentage(lineDTO.getGstPercentage())
                            .totalAmount(lineDTO.getTotalAmount())
                            .build());
                }
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
        List<PurchaseOrderDTO.PurchaseOrderLineDTO> lineDTOs = po.getLines().stream()
                .map(l -> PurchaseOrderDTO.PurchaseOrderLineDTO.builder()
                        .id(l.getId())
                        .itemId(l.getItem().getId())
                        .itemCode(l.getItem().getItemCode())
                        .itemName(l.getItem().getItemName())
                        .quantity(l.getQuantity())
                        .rate(l.getRate())
                        .discountPercentage(l.getDiscountPercentage())
                        .gstPercentage(l.getGstPercentage())
                        .totalAmount(l.getTotalAmount())
                        .build())
                .collect(Collectors.toList());

        return PurchaseOrderDTO.builder()
                .id(po.getId())
                .poNumber(po.getPoNumber())
                .poDate(po.getPoDate())
                .supplierId(po.getSupplier().getId())
                .supplierName(po.getSupplier().getSupplierName())
                .departmentId(po.getDepartment() != null ? po.getDepartment().getId() : null)
                .departmentName(po.getDepartment() != null ? po.getDepartment().getName() : null)
                .expectedDate(po.getExpectedDate())
                .prId(po.getPurchaseRequest() != null ? Arrays.stream(po.getPurchaseRequest().split(","))
                        .map(String::trim)
                        .map(Long::valueOf)
                        .collect(Collectors.toList()) : null)
                .deliveryStoreId(po.getDeliveryStore() != null ? po.getDeliveryStore().getId() : null)
                .deliveryStoreName(po.getDeliveryStore() != null ? po.getDeliveryStore().getValue() : null)
                .paymentTermsId(po.getPaymentTerms() != null ? po.getPaymentTerms().getId() : null)
                .paymentTermsName(po.getPaymentTerms() != null ? po.getPaymentTerms().getValue() : null)
                .requestedBy(po.getRequestedBy())
                .itemCount(po.getItemCount())
                .poNote(po.getPoNote())
                .totalAmount(po.getTotalAmount())
                .lines(lineDTOs)
                .statusId(po.getStatus() != null ? po.getStatus().getId() : null)
                .statusName(po.getStatus() != null ? po.getStatus().getValue() : null)
                .statusCode(po.getStatus() != null ? po.getStatus().getCode() : null)
                .createdAt(po.getCreatedAt())
                .updatedAt(po.getUpdatedAt())
                .build();
    }
}
