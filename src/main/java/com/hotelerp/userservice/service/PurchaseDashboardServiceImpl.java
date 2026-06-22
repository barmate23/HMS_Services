package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.dashboard.PurchaseDashboardDTO;
import com.hotelerp.userservice.entity.*;
import com.hotelerp.userservice.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseDashboardServiceImpl implements PurchaseDashboardService {

    private final SupplierRepository supplierRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final GrnRepository grnRepository;
    private final VendorBillRepository vendorBillRepository;

    @Override
    public StandardResponse<PurchaseDashboardDTO> getDashboardData() {
        try {
            // 1. Stats
            long suppliersCount = supplierRepository.countByIsDeletedFalse();
            
            // Open POs: anything not fully RECEIVED or CANCELLED
            long openPosCount = purchaseOrderRepository.countByStatus_CodeNotInAndIsDeletedFalse(List.of("RECEIVED", "CANCELLED"));
            
            BigDecimal poValue = purchaseOrderRepository.sumTotalAmountByIsDeletedFalse();
            if (poValue == null) poValue = BigDecimal.ZERO;

            BigDecimal inwardValue = grnRepository.sumAcceptedValueByIsDeletedFalse();
            if (inwardValue == null) inwardValue = BigDecimal.ZERO;

            // Payables: anything not PAID
            BigDecimal payables = vendorBillRepository.sumTotalAmountByStatus_CodeNotAndIsDeletedFalse("PAID");
            if (payables == null) payables = BigDecimal.ZERO;

            PurchaseDashboardDTO.PurchaseStats stats = PurchaseDashboardDTO.PurchaseStats.builder()
                    .suppliersCount(suppliersCount)
                    .openPosCount(openPosCount)
                    .poValue(poValue)
                    .inwardValue(inwardValue)
                    .payables(payables)
                    .build();

            // 2. Pending Procurement (Open POs)
            List<PurchaseDashboardDTO.PendingProcurementDTO> pendingProcurement = purchaseOrderRepository.findByIsDeletedFalse().stream()
                    .filter(po -> !List.of("RECEIVED", "CANCELLED").contains(po.getStatus().getCode()))
                    .map(po -> PurchaseDashboardDTO.PendingProcurementDTO.builder()
                            .poNumber(po.getPoNumber())
                            .supplierName(po.getSupplier().getSupplierName())
                            .expectedDate(po.getExpectedDate())
                            .status(po.getStatus().getValue())
                            .statusCode(po.getStatus().getCode())
                            .build())
                    .collect(Collectors.toList());

            // 3. Vendor Bill Queue
            List<PurchaseDashboardDTO.VendorBillQueueDTO> vendorBillQueue = vendorBillRepository.findByIsDeletedFalse().stream()
                    .map(bill -> PurchaseDashboardDTO.VendorBillQueueDTO.builder()
                            .billNumber(bill.getBillNumber())
                            .supplierName(bill.getSupplier().getSupplierName())
                            .dueDate(bill.getDueDate())
                            .amount(bill.getTotalAmount())
                            .status(bill.getStatus().getValue())
                            .statusCode(bill.getStatus().getCode())
                            .build())
                    .collect(Collectors.toList());

            PurchaseDashboardDTO dashboard = PurchaseDashboardDTO.builder()
                    .stats(stats)
                    .pendingProcurement(pendingProcurement)
                    .vendorBillQueue(vendorBillQueue)
                    .build();

            return StandardResponse.success(dashboard, "Purchase dashboard data fetched successfully");
        } catch (Exception e) {
            return StandardResponse.error("Failed to fetch dashboard data", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }
}
