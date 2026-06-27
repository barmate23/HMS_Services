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
    private StockHealthDTO stockHealth;
    private List<ReorderItemDTO> reorderWatch;
    private PrPipelineDTO prPipeline;
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
        private Long openStoreIssuesCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockHealthDTO {
        private Long healthyCount;
        private Long lowStockCount;
        private Long outOfStockCount;
        private Long overstockCount;
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
        private BigDecimal reorderLevel;
        private String unit;
        private String status; // "LOW" or "CRITICAL"
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PrPipelineStageDTO {
        private Long count;
        private BigDecimal value;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PrPipelineDTO {
        private Long totalOpen;
        private PrPipelineStageDTO draft;
        private PrPipelineStageDTO submitted;
        private PrPipelineStageDTO approved;
        private PrPipelineStageDTO ordered;
        private PrPipelineStageDTO rejected;
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
        private String status;
    }
}
