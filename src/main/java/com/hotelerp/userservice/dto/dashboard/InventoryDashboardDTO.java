package com.hotelerp.userservice.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryDashboardDTO {
    private InventoryStats stats;
    private List<ReorderItemDTO> reorderWatch;
    private List<StoreMovementDTO> todayMovement;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InventoryStats {
        private Long totalSkus;
        private Long lowStockCount;
        private BigDecimal totalStockValue;
        private Long openPrsCount;
        private Long refillAlertsCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReorderItemDTO {
        private Long itemId;
        private String itemName;
        private String storeName;
        private BigDecimal onHand;
        private String unit;
        private String status; // "LOW"
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StoreMovementDTO {
        private String issueNo;
        private String department;
        private String itemName;
        private Integer quantity;
        private String unit;
    }
}
