package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.dashboard.InventoryDashboardDTO;
import com.hotelerp.userservice.entity.*;
import com.hotelerp.userservice.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryDashboardServiceImpl implements InventoryDashboardService {

    private final InventoryStockRepository inventoryStockRepository;
    private final PurchaseRequestRepository purchaseRequestRepository;
    private final MinibarConsumptionRepository minibarConsumptionRepository;
    private final StoreIssueRepository storeIssueRepository;
    private final CommonMasterRepository commonMasterRepository;

    @Override
    public StandardResponse<InventoryDashboardDTO> getDashboardData() {
        try {
            // ── 1. Stock Data & Health ──────────────────────────────────────────────────
            List<InventoryStock> allStocks = inventoryStockRepository.findByIsDeletedFalse();
            long totalSkus = allStocks.size();
            BigDecimal totalValue = inventoryStockRepository.calculateTotalStockValue();
            if (totalValue == null) totalValue = BigDecimal.ZERO;

            long healthyCount = 0;
            long lowCount = 0;
            long outOfStockCount = 0;
            long overstockCount = 0;

            for (InventoryStock s : allStocks) {
                BigDecimal onHand = s.getOnHand() != null ? s.getOnHand() : BigDecimal.ZERO;
                BigDecimal reorder = (s.getItemConfig() != null && s.getItemConfig().getReorderLevel() != null)
                        ? s.getItemConfig().getReorderLevel() : BigDecimal.ZERO;
                BigDecimal maxQty = (s.getItemConfig() != null && s.getItemConfig().getMaximumQty() != null)
                        ? s.getItemConfig().getMaximumQty() : new BigDecimal("999999");

                if (onHand.compareTo(BigDecimal.ZERO) <= 0) {
                    outOfStockCount++;
                } else if (onHand.compareTo(reorder) <= 0) {
                    lowCount++;
                } else if (onHand.compareTo(maxQty) > 0) {
                    overstockCount++;
                } else {
                    healthyCount++;
                }
            }

            InventoryDashboardDTO.StockHealthDTO stockHealth = InventoryDashboardDTO.StockHealthDTO.builder()
                    .healthyCount(healthyCount)
                    .lowStockCount(lowCount)
                    .outOfStockCount(outOfStockCount)
                    .overstockCount(overstockCount)
                    .build();

            // ── 2. PR Pipeline & Stats ──────────────────────────────────────────────────
            List<PurchaseRequest> allPrs = purchaseRequestRepository.findByIsDeletedFalse();
            long totalOpenPrs = 0;
            
            InventoryDashboardDTO.PrPipelineStageDTO draft = new InventoryDashboardDTO.PrPipelineStageDTO(0L, BigDecimal.ZERO);
            InventoryDashboardDTO.PrPipelineStageDTO submitted = new InventoryDashboardDTO.PrPipelineStageDTO(0L, BigDecimal.ZERO);
            InventoryDashboardDTO.PrPipelineStageDTO approved = new InventoryDashboardDTO.PrPipelineStageDTO(0L, BigDecimal.ZERO);
            InventoryDashboardDTO.PrPipelineStageDTO ordered = new InventoryDashboardDTO.PrPipelineStageDTO(0L, BigDecimal.ZERO);
            InventoryDashboardDTO.PrPipelineStageDTO rejected = new InventoryDashboardDTO.PrPipelineStageDTO(0L, BigDecimal.ZERO);

            for (PurchaseRequest pr : allPrs) {
                String code = pr.getStatus() != null ? pr.getStatus().getCode() : "";
                BigDecimal amount = pr.getExpectedAmount() != null ? pr.getExpectedAmount() : BigDecimal.ZERO;
                
                if (!"CLOSED".equalsIgnoreCase(code) && !"CANCELLED".equalsIgnoreCase(code)) {
                    totalOpenPrs++;
                }

                switch (code.toUpperCase()) {
                    case "DRAFT":
                        draft.setCount(draft.getCount() + 1);
                        draft.setValue(draft.getValue().add(amount));
                        break;
                    case "SUBMITTED":
                        submitted.setCount(submitted.getCount() + 1);
                        submitted.setValue(submitted.getValue().add(amount));
                        break;
                    case "APPROVED":
                        approved.setCount(approved.getCount() + 1);
                        approved.setValue(approved.getValue().add(amount));
                        break;
                    case "ORDERED":
                        ordered.setCount(ordered.getCount() + 1);
                        ordered.setValue(ordered.getValue().add(amount));
                        break;
                    case "REJECTED":
                        rejected.setCount(rejected.getCount() + 1);
                        rejected.setValue(rejected.getValue().add(amount));
                        break;
                }
            }

            InventoryDashboardDTO.PrPipelineDTO prPipeline = InventoryDashboardDTO.PrPipelineDTO.builder()
                    .totalOpen(totalOpenPrs)
                    .draft(draft)
                    .submitted(submitted)
                    .approved(approved)
                    .ordered(ordered)
                    .rejected(rejected)
                    .build();

            // ── 3. Store Movement & Issues ─────────────────────────────────────────────
            List<StoreIssue> allIssues = storeIssueRepository.findByIsDeletedFalse();
            long openStoreIssuesCount = allIssues.stream()
                    .filter(issue -> issue.getStatus() != null && "OPEN".equalsIgnoreCase(issue.getStatus().getCode()))
                    .count();

            List<InventoryDashboardDTO.StoreMovementDTO> todayMovement = allIssues.stream()
                    .filter(issue -> issue.getIssueDate() != null && issue.getIssueDate().isEqual(LocalDate.now()))
                    .map(issue -> {
                        String uom = (issue.getItem() != null && issue.getItem().getItemConfig() != null && issue.getItem().getItemConfig().getUom() != null)
                                ? issue.getItem().getItemConfig().getUom().getValue() : "";
                        return InventoryDashboardDTO.StoreMovementDTO.builder()
                                .issueNo(issue.getIssueNumber())
                                .department(issue.getDepartment() != null ? issue.getDepartment().getName() : "N/A")
                                .itemName(issue.getItem() != null && issue.getItem().getItemConfig() != null ? issue.getItem().getItemConfig().getItemName() : "N/A")
                                .quantity(issue.getQuantity())
                                .unit(uom)
                                .status(issue.getStatus() != null ? issue.getStatus().getValue() : "OPEN")
                                .build();
                    })
                    .collect(Collectors.toList());

            // ── 4. Final Stats & Reorder Watch ──────────────────────────────────────────
            InventoryDashboardDTO.InventoryStats stats = InventoryDashboardDTO.InventoryStats.builder()
                    .totalSkus(totalSkus)
                    .lowStockCount(lowCount)
                    .totalStockValue(totalValue)
                    .openPrsCount(totalOpenPrs)
                    .openStoreIssuesCount(openStoreIssuesCount)
                    .build();

            List<InventoryDashboardDTO.ReorderItemDTO> reorderWatch = inventoryStockRepository.findLowStockItems().stream()
                    .map(item -> {
                        BigDecimal onHand = item.getOnHand() != null ? item.getOnHand() : BigDecimal.ZERO;
                        BigDecimal reorder = item.getItemConfig() != null && item.getItemConfig().getReorderLevel() != null
                                ? item.getItemConfig().getReorderLevel() : BigDecimal.ZERO;
                        String uom = (item.getItemConfig() != null && item.getItemConfig().getUom() != null)
                                ? item.getItemConfig().getUom().getValue() : "";
                        
                        // Treat as critical if on hand is less than or equal to 50% of reorder level, else LOW
                        String status = onHand.compareTo(reorder.multiply(new BigDecimal("0.5"))) <= 0 ? "CRITICAL" : "LOW";
                        
                        return InventoryDashboardDTO.ReorderItemDTO.builder()
                                .itemId(item.getId())
                                .itemName(item.getItemConfig() != null ? item.getItemConfig().getItemName() : "N/A")
                                .storeName(item.getStore() != null ? item.getStore().getValue() : "N/A")
                                .onHand(onHand)
                                .reorderLevel(reorder)
                                .unit(uom)
                                .status(status)
                                .build();
                    })
                    .collect(Collectors.toList());

            // Assemble DTO
            InventoryDashboardDTO dashboard = InventoryDashboardDTO.builder()
                    .stats(stats)
                    .stockHealth(stockHealth)
                    .reorderWatch(reorderWatch)
                    .prPipeline(prPipeline)
                    .todayMovement(todayMovement)
                    .build();

            return StandardResponse.success(dashboard, "Dashboard data fetched successfully");
        } catch (Exception e) {
            return StandardResponse.error("Failed to fetch dashboard data", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }
}
