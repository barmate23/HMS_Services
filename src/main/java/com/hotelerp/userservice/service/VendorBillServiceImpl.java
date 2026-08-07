package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.VendorBillDTO;
import com.hotelerp.userservice.dto.VendorBillLineDTO;
import com.hotelerp.userservice.entity.*;
import com.hotelerp.userservice.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VendorBillServiceImpl implements VendorBillService {

    private final VendorBillRepository vendorBillRepository;
    private final SupplierRepository supplierRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final CommonMasterRepository commonMasterRepository;
    private final ItemConfigRepository itemConfigRepository;
    private final KitchenIngredientRepository kitchenIngredientRepository;

    @Override
    @Transactional
    public StandardResponse<VendorBillDTO> createVendorBill(VendorBillDTO dto) {
        try {
            Supplier supplier = supplierRepository.findByIdAndIsDeletedFalse(dto.getSupplierId())
                    .orElseThrow(() -> new RuntimeException("Supplier not found"));

            VendorBill bill = VendorBill.builder()
                    .billNumber(dto.getBillNumber())
                    .supplier(supplier)
                    .billDate(dto.getBillDate())
                    .dueDate(dto.getDueDate())
                    .amountBeforeTax(dto.getAmountBeforeTax())
                    .taxAmount(dto.getTaxAmount())
                    .totalAmount(dto.getTotalAmount())
                    .build();

            if (dto.getPurchaseOrderId() != null) {
                PurchaseOrder po = purchaseOrderRepository.findByIdAndIsDeletedFalse(dto.getPurchaseOrderId())
                        .orElseThrow(() -> new RuntimeException("Purchase Order not found"));
                bill.setPurchaseOrder(po);
            }

            if (dto.getStatusId() != null) {
                bill.setStatus(commonMasterRepository.findById(dto.getStatusId())
                        .orElseThrow(() -> new RuntimeException("Status not found")));
            } else {
                commonMasterRepository.findByCategoryAndCode("VENDOR_BILL_STATUS", "PENDING")
                        .ifPresent(bill::setStatus);
            }

            if (dto.getLines() != null && !dto.getLines().isEmpty()) {
                List<VendorBillLine> lines = buildVendorBillLines(bill, dto.getLines());
                bill.getLines().addAll(lines);
            }

            bill = vendorBillRepository.save(bill);
            return StandardResponse.success(convertToDTO(bill), "Vendor Bill created successfully");
        } catch (Exception e) {
            log.error("Error creating Vendor Bill: ", e);
            return StandardResponse.error("Failed to create Vendor Bill", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    @Transactional
    public StandardResponse<VendorBillDTO> updateVendorBill(Long id, VendorBillDTO dto) {
        try {
            VendorBill bill = vendorBillRepository.findByIdAndIsDeletedFalse(id)
                    .orElseThrow(() -> new RuntimeException("Vendor Bill not found"));

            bill.setBillNumber(dto.getBillNumber());
            bill.setBillDate(dto.getBillDate());
            bill.setDueDate(dto.getDueDate());
            bill.setAmountBeforeTax(dto.getAmountBeforeTax());
            bill.setTaxAmount(dto.getTaxAmount());
            bill.setTotalAmount(dto.getTotalAmount());

            if (dto.getSupplierId() != null) {
                Supplier supplier = supplierRepository.findByIdAndIsDeletedFalse(dto.getSupplierId())
                        .orElseThrow(() -> new RuntimeException("Supplier not found"));
                bill.setSupplier(supplier);
            }

            if (dto.getPurchaseOrderId() != null) {
                PurchaseOrder po = purchaseOrderRepository.findByIdAndIsDeletedFalse(dto.getPurchaseOrderId())
                        .orElseThrow(() -> new RuntimeException("Purchase Order not found"));
                bill.setPurchaseOrder(po);
            }

            if (dto.getStatusId() != null) {
                bill.setStatus(commonMasterRepository.findById(dto.getStatusId())
                        .orElseThrow(() -> new RuntimeException("Status not found")));
            }

            if (dto.getLines() != null) {
                bill.getLines().clear();
                bill.getLines().addAll(buildVendorBillLines(bill, dto.getLines()));
            }

            bill = vendorBillRepository.save(bill);
            return StandardResponse.success(convertToDTO(bill), "Vendor Bill updated successfully");
        } catch (Exception e) {
            log.error("Error updating Vendor Bill: ", e);
            return StandardResponse.error("Failed to update Vendor Bill", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    public StandardResponse<VendorBillDTO> getVendorBillById(Long id) {
        try {
            VendorBill bill = vendorBillRepository.findByIdAndIsDeletedFalse(id)
                    .orElseThrow(() -> new RuntimeException("Vendor Bill not found"));
            return StandardResponse.success(convertToDTO(bill), "Vendor Bill fetched successfully");
        } catch (Exception e) {
            return StandardResponse.error("Not found", "NOT_FOUND", e.getMessage());
        }
    }

    @Override
    public StandardResponse<List<VendorBillDTO>> getAllVendorBills() {
        try {
            List<VendorBillDTO> list = vendorBillRepository.findByIsDeletedFalse().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return StandardResponse.success(list, "Vendor Bills fetched successfully");
        } catch (Exception e) {
            return StandardResponse.error("Failed to fetch Vendor Bills", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    @Transactional
    public StandardResponse<Void> deleteVendorBill(Long id) {
        try {
            VendorBill bill = vendorBillRepository.findByIdAndIsDeletedFalse(id)
                    .orElseThrow(() -> new RuntimeException("Vendor Bill not found"));
            bill.setIsDeleted(true);
            vendorBillRepository.save(bill);
            return StandardResponse.success("Vendor Bill deleted successfully");
        } catch (Exception e) {
            return StandardResponse.error("Failed to delete Vendor Bill", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    @Transactional
    public StandardResponse<VendorBillDTO> updateStatus(Long id, Long statusId) {
        try {
            VendorBill bill = vendorBillRepository.findByIdAndIsDeletedFalse(id)
                    .orElseThrow(() -> new RuntimeException("Vendor Bill not found"));
            CommonMaster status = commonMasterRepository.findById(statusId)
                    .orElseThrow(() -> new RuntimeException("Status not found"));
            bill.setStatus(status);
            bill = vendorBillRepository.save(bill);
            return StandardResponse.success(convertToDTO(bill), "Status updated successfully");
        } catch (Exception e) {
            return StandardResponse.error("Failed to update status", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    private List<VendorBillLine> buildVendorBillLines(VendorBill bill, List<VendorBillLineDTO> lineDTOs) {
        if (lineDTOs == null || lineDTOs.isEmpty()) {
            return new ArrayList<>();
        }
        boolean isKitchen = false;
        if (bill.getPurchaseOrder() != null && bill.getPurchaseOrder().getPurchaseItemCategory() != null) {
            String val = bill.getPurchaseOrder().getPurchaseItemCategory().getValue();
            String code = bill.getPurchaseOrder().getPurchaseItemCategory().getCode();
            if ((val != null && val.equalsIgnoreCase("KITCHEN")) ||
                (code != null && code.equalsIgnoreCase("KITCHEN"))) {
                isKitchen = true;
            }
        }

        List<VendorBillLine> lines = new ArrayList<>();
        for (VendorBillLineDTO lineDTO : lineDTOs) {
            Long itemId = lineDTO.getItemId();
            if (itemId == null) {
                throw new RuntimeException("Item ID is required for line item");
            }

            VendorBillLine.VendorBillLineBuilder lineBuilder = VendorBillLine.builder()
                    .vendorBill(bill)
                    .receivedQuantity(lineDTO.getReceivedQuantity())
                    .rate(lineDTO.getRate())
                    .totalAmount(lineDTO.getTotalAmount());

            if (isKitchen) {
                KitchenIngredient ingredient = kitchenIngredientRepository.findById(itemId)
                        .orElseThrow(() -> new RuntimeException("Kitchen Ingredient not found with ID: " + itemId));
                lineBuilder.kitchenIngredient(ingredient);
            } else {
                ItemConfig item = itemConfigRepository.findById(itemId)
                        .orElseThrow(() -> new RuntimeException("Item not found with ID: " + itemId));
                lineBuilder.item(item);
            }
            lines.add(lineBuilder.build());
        }
        return lines;
    }

    private VendorBillDTO convertToDTO(VendorBill bill) {
        return VendorBillDTO.builder()
                .id(bill.getId())
                .billNumber(bill.getBillNumber())
                .supplierId(bill.getSupplier() != null ? bill.getSupplier().getId() : null)
                .supplierName(bill.getSupplier() != null ? bill.getSupplier().getSupplierName() : null)
                .purchaseOrderId(bill.getPurchaseOrder() != null ? bill.getPurchaseOrder().getId() : null)
                .poNumber(bill.getPurchaseOrder() != null ? bill.getPurchaseOrder().getPoNumber() : null)
                .billDate(bill.getBillDate())
                .dueDate(bill.getDueDate())
                .amountBeforeTax(bill.getAmountBeforeTax())
                .taxAmount(bill.getTaxAmount())
                .totalAmount(bill.getTotalAmount())
                .statusId(bill.getStatus() != null ? bill.getStatus().getId() : null)
                .statusName(bill.getStatus() != null ? bill.getStatus().getValue() : null)
                .statusCode(bill.getStatus() != null ? bill.getStatus().getCode() : null)
                .createdAt(bill.getCreatedAt())
                .updatedAt(bill.getUpdatedAt())
                .lines(bill.getLines() != null ? bill.getLines().stream().map(this::convertLineToDTO).collect(Collectors.toList()) : new ArrayList<>())
                .build();
    }

    private VendorBillLineDTO convertLineToDTO(VendorBillLine line) {
        VendorBillLineDTO.VendorBillLineDTOBuilder builder = VendorBillLineDTO.builder()
                .id(line.getId())
                .vendorBillId(line.getVendorBill() != null ? line.getVendorBill().getId() : null)
                .receivedQuantity(line.getReceivedQuantity())
                .rate(line.getRate())
                .totalAmount(line.getTotalAmount())
                .createdAt(line.getCreatedAt());

        if (line.getKitchenIngredient() != null) {
            builder.itemId(line.getKitchenIngredient().getId())
                   .itemCode(line.getKitchenIngredient().getIngredientCode())
                   .itemName(line.getKitchenIngredient().getIngredientName());
        } else if (line.getItem() != null) {
            builder.itemId(line.getItem().getId())
                   .itemCode(line.getItem().getItemCode())
                   .itemName(line.getItem().getItemName());
        }

        return builder.build();
    }
}
