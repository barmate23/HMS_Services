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
            // 1. Stats
            long totalSkus = inventoryStockRepository.countByIsDeletedFalse();
            long lowStockCount = inventoryStockRepository.countLowStockItems();
            BigDecimal totalValue = inventoryStockRepository.calculateTotalStockValue();
            if (totalValue == null) totalValue = BigDecimal.ZERO;

            // Open PRs: count where status is DRAFT or SUBMITTED
            long openPrs = purchaseRequestRepository.countByStatus_CodeInAndIsDeletedFalse(List.of("DRAFT", "SUBMITTED"));
            
            // Refill Alerts: count MinibarConsumption status REFILL
            long refillAlerts = minibarConsumptionRepository.countByStatus_CodeAndIsDeletedFalse("REFILL");

            InventoryDashboardDTO.InventoryStats stats = InventoryDashboardDTO.InventoryStats.builder()
                    .totalSkus(totalSkus)
                    .lowStockCount(lowStockCount)
                    .totalStockValue(totalValue)
                    .openPrsCount(openPrs)
                    .refillAlertsCount(refillAlerts)
                    .build();

            // 2. Reorder Watch
            List<InventoryDashboardDTO.ReorderItemDTO> reorderWatch = inventoryStockRepository.findLowStockItems().stream()
                    .map(item -> InventoryDashboardDTO.ReorderItemDTO.builder()
                            .itemId(item.getId())
                            .itemName(item.getItemConfig().getItemName())
                            .storeName(item.getStore() != null ? item.getStore().getValue() : "N/A")
                            .onHand(item.getOnHand())
                            .unit(item.getItemConfig().getUnitCost().toString())
                            .status("LOW")
                            .build())
                    .collect(Collectors.toList());

            // 3. Today's Store Movement
            List<InventoryDashboardDTO.StoreMovementDTO> todayMovement = storeIssueRepository.findByIssueDateAndIsDeletedFalse(LocalDate.now()).stream()
                    .map(issue -> InventoryDashboardDTO.StoreMovementDTO.builder()
                            .issueNo(issue.getIssueNumber())
                            .department(issue.getDepartment() != null ? issue.getDepartment().getValue() : "N/A")
                            .itemName(issue.getItem() != null ? issue.getItem().getItemConfig().getItemName() : "N/A")
                            .quantity(issue.getQuantity())
                            .unit(issue.getItem() != null ? issue.getItem().getItemConfig().getUnitCost().toString() : "")
                            .build())
                    .collect(Collectors.toList());

            InventoryDashboardDTO dashboard = InventoryDashboardDTO.builder()
                    .stats(stats)
                    .reorderWatch(reorderWatch)
                    .todayMovement(todayMovement)
                    .build();

            return StandardResponse.success(dashboard, "Dashboard data fetched successfully");
        } catch (Exception e) {
            return StandardResponse.error("Failed to fetch dashboard data", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }
}
