package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.InventoryStockDTO;
import com.hotelerp.userservice.entity.CommonMaster;
import com.hotelerp.userservice.entity.InventoryStock;
import com.hotelerp.userservice.repository.CommonMasterRepository;
import com.hotelerp.userservice.repository.InventoryStockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryStockServiceImpl implements InventoryStockService {

    private final InventoryStockRepository inventoryStockRepository;
    private final CommonMasterRepository commonMasterRepository;

    @Override
    @Transactional
    public StandardResponse<InventoryStockDTO> createStockItem(InventoryStockDTO dto) {
        try {
            InventoryStock stock = InventoryStock.builder()
                    .itemCode(dto.getItemCode())
                    .itemName(dto.getItemName())
                    .onHand(dto.getOnHand() != null ? dto.getOnHand() : BigDecimal.ZERO)
                    .unit(dto.getUnit())
                    .reorderLevel(dto.getReorderLevel())
                    .parLevel(dto.getParLevel())
                    .unitCost(dto.getUnitCost())
                    .build();

            if (dto.getCategoryId() != null) {
                stock.setCategory(commonMasterRepository.findById(dto.getCategoryId())
                        .orElseThrow(() -> new RuntimeException("Category not found")));
            }

            if (dto.getStoreId() != null) {
                stock.setStore(commonMasterRepository.findById(dto.getStoreId())
                        .orElseThrow(() -> new RuntimeException("Store not found")));
            }

            if (dto.getStatusId() != null) {
                stock.setStatus(commonMasterRepository.findById(dto.getStatusId())
                        .orElseThrow(() -> new RuntimeException("Status not found")));
            }

            stock = inventoryStockRepository.save(stock);
            return StandardResponse.success(convertToDTO(stock), "Stock item created successfully");
        } catch (Exception e) {
            log.error("Error creating stock item: ", e);
            return StandardResponse.error("Failed to create stock item", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    @Transactional
    public StandardResponse<InventoryStockDTO> updateStockItem(Long id, InventoryStockDTO dto) {
        try {
            InventoryStock stock = inventoryStockRepository.findByIdAndIsDeletedFalse(id)
                    .orElseThrow(() -> new RuntimeException("Stock item not found"));

            stock.setItemName(dto.getItemName());
            stock.setOnHand(dto.getOnHand());
            stock.setUnit(dto.getUnit());
            stock.setReorderLevel(dto.getReorderLevel());
            stock.setParLevel(dto.getParLevel());
            stock.setUnitCost(dto.getUnitCost());

            if (dto.getCategoryId() != null) {
                stock.setCategory(commonMasterRepository.findById(dto.getCategoryId())
                        .orElseThrow(() -> new RuntimeException("Category not found")));
            }

            if (dto.getStoreId() != null) {
                stock.setStore(commonMasterRepository.findById(dto.getStoreId())
                        .orElseThrow(() -> new RuntimeException("Store not found")));
            }

            if (dto.getStatusId() != null) {
                stock.setStatus(commonMasterRepository.findById(dto.getStatusId())
                        .orElseThrow(() -> new RuntimeException("Status not found")));
            }

            stock = inventoryStockRepository.save(stock);
            return StandardResponse.success(convertToDTO(stock), "Stock item updated successfully");
        } catch (Exception e) {
            log.error("Error updating stock item: ", e);
            return StandardResponse.error("Failed to update stock item", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    public StandardResponse<InventoryStockDTO> getStockItemById(Long id) {
        try {
            InventoryStock stock = inventoryStockRepository.findByIdAndIsDeletedFalse(id)
                    .orElseThrow(() -> new RuntimeException("Stock item not found"));
            return StandardResponse.success(convertToDTO(stock), "Stock item fetched successfully");
        } catch (Exception e) {
            return StandardResponse.error("Failed to fetch stock item", "NOT_FOUND", e.getMessage());
        }
    }

    @Override
    public StandardResponse<List<InventoryStockDTO>> getAllStockItems() {
        try {
            List<InventoryStockDTO> list = inventoryStockRepository.findByIsDeletedFalse().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return StandardResponse.success(list, "Stock items fetched successfully");
        } catch (Exception e) {
            return StandardResponse.error("Failed to fetch stock items", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    @Transactional
    public StandardResponse<Void> deleteStockItem(Long id) {
        try {
            InventoryStock stock = inventoryStockRepository.findByIdAndIsDeletedFalse(id)
                    .orElseThrow(() -> new RuntimeException("Stock item not found"));
            stock.setIsDeleted(true);
            inventoryStockRepository.save(stock);
            return StandardResponse.success("Stock item deleted successfully");
        } catch (Exception e) {
            return StandardResponse.error("Failed to delete stock item", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    @Transactional
    public StandardResponse<InventoryStockDTO> updateStockStatus(Long id, Long statusId) {
        try {
            InventoryStock stock = inventoryStockRepository.findByIdAndIsDeletedFalse(id)
                    .orElseThrow(() -> new RuntimeException("Stock item not found"));

            CommonMaster status = commonMasterRepository.findById(statusId)
                    .orElseThrow(() -> new RuntimeException("Status master data not found"));

            stock.setStatus(status);
            stock = inventoryStockRepository.save(stock);
            return StandardResponse.success(convertToDTO(stock), "Stock status updated successfully");
        } catch (Exception e) {
            log.error("Error updating stock status: ", e);
            return StandardResponse.error("Failed to update stock status", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    private InventoryStockDTO convertToDTO(InventoryStock stock) {
        BigDecimal value = BigDecimal.ZERO;
        if (stock.getOnHand() != null && stock.getUnitCost() != null) {
            value = stock.getOnHand().multiply(stock.getUnitCost());
        }

        return InventoryStockDTO.builder()
                .id(stock.getId())
                .itemCode(stock.getItemCode())
                .itemName(stock.getItemName())
                .categoryId(stock.getCategory() != null ? stock.getCategory().getId() : null)
                .categoryName(stock.getCategory() != null ? stock.getCategory().getValue() : null)
                .storeId(stock.getStore() != null ? stock.getStore().getId() : null)
                .storeName(stock.getStore() != null ? stock.getStore().getValue() : null)
                .onHand(stock.getOnHand())
                .unit(stock.getUnit())
                .reorderLevel(stock.getReorderLevel())
                .parLevel(stock.getParLevel())
                .unitCost(stock.getUnitCost())
                .totalValue(value)
                .statusId(stock.getStatus() != null ? stock.getStatus().getId() : null)
                .statusName(stock.getStatus() != null ? stock.getStatus().getValue() : null)
                .createdAt(stock.getCreatedAt())
                .updatedAt(stock.getUpdatedAt())
                .build();
    }
}
