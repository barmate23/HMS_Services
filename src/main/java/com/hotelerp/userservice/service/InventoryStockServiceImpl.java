package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.InventoryStockDTO;
import com.hotelerp.userservice.entity.CommonMaster;
import com.hotelerp.userservice.entity.InventoryStock;
import com.hotelerp.userservice.repository.CommonMasterRepository;
import com.hotelerp.userservice.repository.InventoryStockRepository;
import com.hotelerp.userservice.repository.ItemConfigRepository;
import com.hotelerp.userservice.entity.ItemConfig;
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
    private final ItemConfigRepository itemConfigRepository;

    @Override
    @Transactional
    public StandardResponse<InventoryStockDTO> createStockItem(InventoryStockDTO dto) {
        try {
            if (dto.getItemConfigId() == null) {
                return StandardResponse.error("Item Config ID is required", "BAD_REQUEST", "Item Config ID is null");
            }

            ItemConfig itemConfig = itemConfigRepository.findById(dto.getItemConfigId())
                    .orElseThrow(() -> new RuntimeException("Item Config not found"));

            InventoryStock stock = InventoryStock.builder()
                    .itemConfig(itemConfig)
                    .onHand(dto.getOnHand() != null ? dto.getOnHand() : BigDecimal.ZERO)
                    .build();

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

            if (dto.getItemConfigId() != null) {
                ItemConfig itemConfig = itemConfigRepository.findById(dto.getItemConfigId())
                        .orElseThrow(() -> new RuntimeException("Item Config not found"));
                stock.setItemConfig(itemConfig);
            }

            stock.setOnHand(dto.getOnHand());
            stock.setMinimumQty(dto.getMinimumQty());
            stock.setMaximumQty(dto.getMaximumQty());

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
            e.printStackTrace();
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
            return StandardResponse.success(null, "Stock item deleted successfully");
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
        com.hotelerp.userservice.entity.ItemConfig item = stock.getItemConfig();
        BigDecimal value = BigDecimal.ZERO;
        if (stock.getOnHand() != null && item != null && item.getUnitCost() != null) {
            value = stock.getOnHand().multiply(item.getUnitCost());
        }

        return InventoryStockDTO.builder()
                .id(stock.getId())
                .itemConfigId(item != null ? item.getId() : null)
                .itemCode(item != null ? item.getItemCode() : null)
                .itemName(item != null ? item.getItemName() : null)
                .categoryId(item != null && item.getCategory() != null ? item.getCategory().getId() : null)
                .categoryName(item != null && item.getCategory() != null ? item.getCategory().getValue() : null)
                .storeId(stock.getStore() != null ? stock.getStore().getId() : null)
                .storeName(stock.getStore() != null ? stock.getStore().getValue() : null)
                .onHand(stock.getOnHand())
                .minimumQty(stock.getMinimumQty())
                .maximumQty(stock.getMaximumQty())
                .unit(item != null && item.getUom() != null ? item.getUom().getValue() : null)
                .reorderLevel(item != null ? item.getReorderLevel() : null)
                .parLevel(item != null ? item.getMaxStockLevel() : null)
                .unitCost(item != null ? item.getUnitCost() : null)
                .totalValue(value)
                .statusId(stock.getStatus() != null ? stock.getStatus().getId() : null)
                .statusName(stock.getStatus() != null ? stock.getStatus().getValue() : null)
                .createdAt(stock.getCreatedAt())
                .updatedAt(stock.getUpdatedAt())
                .build();
    }
}
