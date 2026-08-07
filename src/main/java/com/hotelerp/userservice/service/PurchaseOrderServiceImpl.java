package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.PurchaseOrderDTO;
import com.hotelerp.userservice.entity.*;
import com.hotelerp.userservice.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    private final KitchenIngredientRepository kitchenIngredientRepository;
    private final PurchaseOrderLineRepository purchaseOrderLineRepository;

    private String generatePoNumber(LocalDate poDate) {
        LocalDate date = poDate != null ? poDate : LocalDate.now();
        String prefix = String.format("PO-%d-%02d%02d", date.getYear(), date.getMonthValue(), date.getDayOfMonth());
        long count = purchaseOrderRepository.countByPoNumberPrefix(prefix);
        return String.format("%s%04d", prefix, count + 1);
    }

    @Override
    @Transactional
    public StandardResponse<PurchaseOrderDTO> createPurchaseOrder(PurchaseOrderDTO dto) {
        try {
            Supplier supplier = supplierRepository.findByIdAndIsDeletedFalse(dto.getSupplierId())
                    .orElseThrow(() -> new RuntimeException("Supplier not found"));

            LocalDate poDate = dto.getPoDate() != null ? dto.getPoDate() : LocalDate.now();
            String poNumber = (dto.getPoNumber() != null && !dto.getPoNumber().isBlank())
                    ? dto.getPoNumber().trim()
                    : generatePoNumber(poDate);

            PurchaseOrder po = PurchaseOrder.builder()
                    .poNumber(poNumber)
                    .poDate(poDate)
                    .supplier(supplier)
                    .expectedDate(dto.getExpectedDate())
                    .itemCount(dto.getItemCount())
                    .poNote(dto.getPoNote())
                    .totalAmount(dto.getTotalAmount())
                    .shippingFreightRate(dto.getShippingFreightRate())
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

            if (dto.getPurchaseItemCategoryId() != null) {
                po.setPurchaseItemCategory(commonMasterRepository.findById(dto.getPurchaseItemCategoryId())
                        .orElseThrow(() -> new RuntimeException("Purchase Item Category not found")));
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
                List<PurchaseOrderLine> lines = buildPoLines(po, dto.getLines());
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
            po.setShippingFreightRate(dto.getShippingFreightRate());
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

            if (dto.getPurchaseItemCategoryId() != null) {
                po.setPurchaseItemCategory(commonMasterRepository.findById(dto.getPurchaseItemCategoryId())
                        .orElseThrow(() -> new RuntimeException("Purchase Item Category not found")));
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
                po.getLines().addAll(buildPoLines(po, dto.getLines()));
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

    private List<PurchaseOrderLine> buildPoLines(PurchaseOrder po, List<PurchaseOrderDTO.PurchaseOrderLineDTO> lineDTOs) {
        if (lineDTOs == null || lineDTOs.isEmpty()) {
            return new ArrayList<>();
        }
        boolean isKitchen = false;
        if (po.getPurchaseItemCategory() != null) {
            String val = po.getPurchaseItemCategory().getValue();
            String code = po.getPurchaseItemCategory().getCode();
            if ((val != null && val.equalsIgnoreCase("KITCHEN")) ||
                (code != null && code.equalsIgnoreCase("KITCHEN"))) {
                isKitchen = true;
            }
        }

        List<PurchaseOrderLine> lines = new ArrayList<>();
        for (PurchaseOrderDTO.PurchaseOrderLineDTO lineDTO : lineDTOs) {
            PurchaseOrderLine.PurchaseOrderLineBuilder lineBuilder = PurchaseOrderLine.builder()
                    .purchaseOrder(po)
                    .quantity(lineDTO.getQuantity())
                    .rate(lineDTO.getRate())
                    .discountPercentage(lineDTO.getDiscountPercentage())
                    .gstPercentage(lineDTO.getGstPercentage())
                    .totalAmount(lineDTO.getTotalAmount());

            if (isKitchen || lineDTO.getKitchenIngredientId() != null) {
                Long ingId = lineDTO.getKitchenIngredientId() != null ? lineDTO.getKitchenIngredientId() : lineDTO.getItemId();
                if (ingId == null) {
                    throw new RuntimeException("Kitchen Ingredient ID is required for line item");
                }
                KitchenIngredient ingredient = kitchenIngredientRepository.findById(ingId)
                        .orElseThrow(() -> new RuntimeException("Kitchen Ingredient not found with ID: " + ingId));
                lineBuilder.kitchenIngredient(ingredient);
            } else {
                if (lineDTO.getItemId() == null) {
                    throw new RuntimeException("Item ID is required for line item");
                }
                ItemConfig item = itemConfigRepository.findById(lineDTO.getItemId())
                        .orElseThrow(() -> new RuntimeException("Item not found with ID: " + lineDTO.getItemId()));
                lineBuilder.item(item);
            }
            lines.add(lineBuilder.build());
        }
        return lines;
    }

    private PurchaseOrderDTO convertToDTO(PurchaseOrder po) {
        List<PurchaseOrderDTO.PurchaseOrderLineDTO> lineDTOs = po.getLines().stream()
                .map(l -> {
                    PurchaseOrderDTO.PurchaseOrderLineDTO.PurchaseOrderLineDTOBuilder builder = PurchaseOrderDTO.PurchaseOrderLineDTO.builder()
                            .id(l.getId())
                            .quantity(l.getQuantity())
                            .rate(l.getRate())
                            .discountPercentage(l.getDiscountPercentage())
                            .gstPercentage(l.getGstPercentage())
                            .totalAmount(l.getTotalAmount());

                    if (l.getKitchenIngredient() != null) {
                        builder.kitchenIngredientId(l.getKitchenIngredient().getId())
                               .itemId(l.getKitchenIngredient().getId())
                               .itemCode(l.getKitchenIngredient().getIngredientCode())
                               .itemName(l.getKitchenIngredient().getIngredientName());
                    } else if (l.getItem() != null) {
                        builder.itemId(l.getItem().getId())
                               .itemCode(l.getItem().getItemCode())
                               .itemName(l.getItem().getItemName());
                    }

                    return builder.build();
                })
                .collect(Collectors.toList());

        return PurchaseOrderDTO.builder()
                .id(po.getId())
                .poNumber(po.getPoNumber())
                .poDate(po.getPoDate())
                .supplierId(po.getSupplier() != null ? po.getSupplier().getId() : null)
                .supplierName(po.getSupplier() != null ? po.getSupplier().getSupplierName() : null)
                .departmentId(po.getDepartment() != null ? po.getDepartment().getId() : null)
                .departmentName(po.getDepartment() != null ? po.getDepartment().getName() : null)
                .expectedDate(po.getExpectedDate())
                .prId(po.getPurchaseRequest() != null && !po.getPurchaseRequest().isBlank() ? Arrays.stream(po.getPurchaseRequest().split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .map(Long::valueOf)
                        .collect(Collectors.toList()) : null)
                .deliveryStoreId(po.getDeliveryStore() != null ? po.getDeliveryStore().getId() : null)
                .deliveryStoreName(po.getDeliveryStore() != null ? po.getDeliveryStore().getValue() : null)
                .paymentTermsId(po.getPaymentTerms() != null ? po.getPaymentTerms().getId() : null)
                .paymentTermsName(po.getPaymentTerms() != null ? po.getPaymentTerms().getValue() : null)
                .purchaseItemCategoryId(po.getPurchaseItemCategory() != null ? po.getPurchaseItemCategory().getId() : null)
                .purchaseItemCategoryName(po.getPurchaseItemCategory() != null ? po.getPurchaseItemCategory().getValue() : null)
                .purchaseItemCategoryCode(po.getPurchaseItemCategory() != null ? po.getPurchaseItemCategory().getCode() : null)
                .requestedBy(po.getRequestedBy())
                .itemCount(po.getItemCount())
                .poNote(po.getPoNote())
                .totalAmount(po.getTotalAmount())
                .shippingFreightRate(po.getShippingFreightRate())
                .lines(lineDTOs)
                .statusId(po.getStatus() != null ? po.getStatus().getId() : null)
                .statusName(po.getStatus() != null ? po.getStatus().getValue() : null)
                .statusCode(po.getStatus() != null ? po.getStatus().getCode() : null)
                .createdAt(po.getCreatedAt())
                .updatedAt(po.getUpdatedAt())
                .build();
    }
}
