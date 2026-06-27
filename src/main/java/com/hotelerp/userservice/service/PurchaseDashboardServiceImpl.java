package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.dashboard.PurchaseDashboardDTO;
import com.hotelerp.userservice.entity.InventoryStock;
import com.hotelerp.userservice.repository.InventoryStockRepository;
import com.hotelerp.userservice.repository.PurchaseOrderRepository;
import com.hotelerp.userservice.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseDashboardServiceImpl implements PurchaseDashboardService {

    private final SupplierRepository supplierRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final InventoryStockRepository inventoryStockRepository;

    // PO status codes used across the dashboard
    private static final List<String> OPEN_STATUSES_EXCLUDED = List.of("RECEIVED", "CANCELLED", "CLOSED");

    @Override
    public StandardResponse<PurchaseDashboardDTO> getDashboardData() {
        try {

            // ── 1. KPI Stats ────────────────────────────────────────────────────
            long suppliersCount = supplierRepository.countByIsDeletedFalse();

            long openPosCount = purchaseOrderRepository
                    .countByStatus_CodeNotInAndIsDeletedFalse(OPEN_STATUSES_EXCLUDED);

            BigDecimal poValue = purchaseOrderRepository.sumTotalAmountByIsDeletedFalse();
            if (poValue == null) poValue = BigDecimal.ZERO;

            long lowStockCount = inventoryStockRepository.countLowStockItems();

            PurchaseDashboardDTO.PurchaseStats stats = PurchaseDashboardDTO.PurchaseStats.builder()
                    .suppliersCount(suppliersCount)
                    .openPosCount(openPosCount)
                    .poValue(poValue)
                    .lowStockItemsCount(lowStockCount)
                    .build();

            // ── 2. Pending Procurement ───────────────────────────────────────────
            List<PurchaseDashboardDTO.PendingProcurementDTO> pendingProcurement =
                    purchaseOrderRepository.findByIsDeletedFalse().stream()
                            .filter(po -> !OPEN_STATUSES_EXCLUDED.contains(po.getStatus().getCode()))
                            .map(po -> PurchaseDashboardDTO.PendingProcurementDTO.builder()
                                    .poId(po.getId())
                                    .poNumber(po.getPoNumber())
                                    .supplierName(po.getSupplier().getSupplierName())
                                    .expectedDate(po.getExpectedDate())
                                    .status(po.getStatus().getValue())
                                    .statusCode(po.getStatus().getCode())
                                    .build())
                            .collect(Collectors.toList());

            // ── 3. Reorder & Low Stock Alerts ───────────────────────────────────
            List<PurchaseDashboardDTO.LowStockAlertDTO> lowStockAlerts =
                    inventoryStockRepository.findLowStockItems().stream()
                            .map(stock -> PurchaseDashboardDTO.LowStockAlertDTO.builder()
                                    .itemConfigId(stock.getItemConfig().getId())
                                    .itemCode(stock.getItemConfig().getItemCode())
                                    .itemName(stock.getItemConfig().getItemName())
                                    .currentStock(stock.getOnHand())
                                    .reorderLevel(stock.getItemConfig().getReorderLevel())
                                    .uom(stock.getItemConfig().getUom() != null
                                            ? stock.getItemConfig().getUom().getValue()
                                            : null)
                                    .build())
                            .collect(Collectors.toList());

            // ── 4. Supplier Categories ───────────────────────────────────────────
            // Step 4a: supplier counts per category
            Map<String, Long> supplierCountByCategory = new LinkedHashMap<>();
            supplierRepository.countSuppliersByCategory()
                    .forEach(row -> supplierCountByCategory.put((String) row[0], (Long) row[1]));

            // Step 4b: PO value sum per category
            Map<String, BigDecimal> poValueByCategory = new LinkedHashMap<>();
            purchaseOrderRepository.sumPoValueBySupplierCategory()
                    .forEach(row -> poValueByCategory.put(
                            (String) row[0],
                            row[1] != null ? (BigDecimal) row[1] : BigDecimal.ZERO));

            // Step 4c: Merge into single list (all categories that have suppliers)
            List<PurchaseDashboardDTO.SupplierCategoryDTO> supplierCategories =
                    supplierCountByCategory.entrySet().stream()
                            .map(entry -> PurchaseDashboardDTO.SupplierCategoryDTO.builder()
                                    .categoryName(entry.getKey())
                                    .supplierCount(entry.getValue())
                                    .totalPoValue(poValueByCategory.getOrDefault(entry.getKey(), BigDecimal.ZERO))
                                    .build())
                            .collect(Collectors.toList());

            // ── 5. Procurement Pipeline ──────────────────────────────────────────
            long draftCount        = purchaseOrderRepository.countByStatus_CodeAndIsDeletedFalse("DRAFT");
            long approvedCount     = purchaseOrderRepository.countByStatus_CodeAndIsDeletedFalse("APPROVED");
            long partialCount      = purchaseOrderRepository.countByStatus_CodeAndIsDeletedFalse("PARTIALLY_RECEIVED");
            long closedCount       = purchaseOrderRepository.countByStatus_CodeAndIsDeletedFalse("CLOSED");
            long totalPos          = purchaseOrderRepository.countByStatus_CodeNotInAndIsDeletedFalse(List.of());

            PurchaseDashboardDTO.ProcurementPipeline pipeline = PurchaseDashboardDTO.ProcurementPipeline.builder()
                    .totalPos(totalPos)
                    .draft(draftCount)
                    .approved(approvedCount)
                    .partiallyReceived(partialCount)
                    .closed(closedCount)
                    .build();

            // ── Assemble ─────────────────────────────────────────────────────────
            PurchaseDashboardDTO dashboard = PurchaseDashboardDTO.builder()
                    .stats(stats)
                    .pendingProcurement(pendingProcurement)
                    .lowStockAlerts(lowStockAlerts)
                    .supplierCategories(supplierCategories)
                    .procurementPipeline(pipeline)
                    .build();

            return StandardResponse.success(dashboard, "Purchase dashboard data fetched successfully");

        } catch (Exception e) {
            return StandardResponse.error("Failed to fetch dashboard data", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }
}
