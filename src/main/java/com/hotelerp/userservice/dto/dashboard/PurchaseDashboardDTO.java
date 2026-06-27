package com.hotelerp.userservice.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseDashboardDTO {

    private PurchaseStats stats;
    private List<PendingProcurementDTO> pendingProcurement;
    private List<LowStockAlertDTO> lowStockAlerts;
    private List<SupplierCategoryDTO> supplierCategories;
    private ProcurementPipeline procurementPipeline;

    // ── 1. KPI Stats ───────────────────────────────────────────────────────────
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PurchaseStats {
        private Long suppliersCount;
        private Long openPosCount;
        private BigDecimal poValue;
        private Long lowStockItemsCount;
    }

    // ── 2. Pending Procurement table ───────────────────────────────────────────
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PendingProcurementDTO {
        private Long poId;
        private String poNumber;
        private String supplierName;
        private LocalDate expectedDate;
        private String status;
        private String statusCode;
    }

    // ── 3. Reorder & Low Stock Alerts ─────────────────────────────────────────
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LowStockAlertDTO {
        private Long itemConfigId;
        private String itemCode;
        private String itemName;
        private BigDecimal currentStock;   // onHand from InventoryStock
        private BigDecimal reorderLevel;   // reorderLevel from ItemConfig
        private String uom;                // unit of measure label
    }

    // ── 4. Supplier Categories ─────────────────────────────────────────────────
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SupplierCategoryDTO {
        private String categoryName;
        private Long supplierCount;
        private BigDecimal totalPoValue;   // sum of PO amounts for this category
    }

    // ── 5. Procurement Pipeline ────────────────────────────────────────────────
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProcurementPipeline {
        private Long totalPos;
        private Long draft;
        private Long approved;
        private Long partiallyReceived;
        private Long closed;
    }
}
