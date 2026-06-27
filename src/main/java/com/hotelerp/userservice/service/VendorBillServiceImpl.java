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
            final VendorBill finalBill = bill;
            if (dto.getLines() != null && !dto.getLines().isEmpty()) {
                List<VendorBillLine> lines = dto.getLines().stream().map(lineDto -> {
                    ItemConfig item = itemConfigRepository.findById(lineDto.getItemId())
                            .orElseThrow(() -> new RuntimeException("Item not found: " + lineDto.getItemId()));
                    return VendorBillLine.builder()
                            .vendorBill(finalBill)
                            .item(item)
                            .receivedQuantity(lineDto.getReceivedQuantity())
                            .rate(lineDto.getRate())
                            .totalAmount(lineDto.getTotalAmount())
                            .build();
                }).collect(Collectors.toList());
                finalBill.getLines().addAll(lines);
            }

            bill = vendorBillRepository.save(finalBill);
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
                VendorBill finalRef = bill;
                List<VendorBillLine> newLines = dto.getLines().stream().map(lineDto -> {
                    ItemConfig item = itemConfigRepository.findById(lineDto.getItemId())
                            .orElseThrow(() -> new RuntimeException("Item not found: " + lineDto.getItemId()));
                    return VendorBillLine.builder()
                            .vendorBill(finalRef)
                            .item(item)
                            .receivedQuantity(lineDto.getReceivedQuantity())
                            .rate(lineDto.getRate())
                            .totalAmount(lineDto.getTotalAmount())
                            .build();
                }).collect(Collectors.toList());
                bill.getLines().addAll(newLines);
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

    private VendorBillDTO convertToDTO(VendorBill bill) {
        return VendorBillDTO.builder()
                .id(bill.getId())
                .billNumber(bill.getBillNumber())
                .supplierId(bill.getSupplier().getId())
                .supplierName(bill.getSupplier().getSupplierName())
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
                .lines(bill.getLines() != null ? bill.getLines().stream().map(this::convertLineToDTO).collect(Collectors.toList()) : new java.util.ArrayList<>())
                .build();
    }

    private VendorBillLineDTO convertLineToDTO(VendorBillLine line) {
        return VendorBillLineDTO.builder()
                .id(line.getId())
                .vendorBillId(line.getVendorBill() != null ? line.getVendorBill().getId() : null)
                .itemId(line.getItem() != null ? line.getItem().getId() : null)
                .itemName(line.getItem() != null ? line.getItem().getItemName() : null)
                .receivedQuantity(line.getReceivedQuantity())
                .rate(line.getRate())
                .totalAmount(line.getTotalAmount())
                .createdAt(line.getCreatedAt())
                .build();
    }
}
