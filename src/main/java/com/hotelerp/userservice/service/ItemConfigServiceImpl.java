package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.ItemConfigDTO;
import com.hotelerp.userservice.entity.CommonMaster;
import com.hotelerp.userservice.entity.ItemConfig;
import com.hotelerp.userservice.repository.CommonMasterRepository;
import com.hotelerp.userservice.repository.ItemConfigRepository;
import com.hotelerp.userservice.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemConfigServiceImpl implements ItemConfigService {

    private final ItemConfigRepository itemConfigRepository;
    private final CommonMasterRepository commonMasterRepository;

    @Override
    @Transactional
    public StandardResponse<ItemConfigDTO> createItem(ItemConfigDTO dto) {
        try {
            CommonMaster category = null;
            if (dto.getCategoryId() != null) {
                category = commonMasterRepository.findById(dto.getCategoryId())
                        .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + dto.getCategoryId()));
            }

            CommonMaster uom = null;
            if (dto.getUomId() != null) {
                uom = commonMasterRepository.findById(dto.getUomId())
                        .orElseThrow(() -> new ResourceNotFoundException("UOM not found with ID: " + dto.getUomId()));
            }

            ItemConfig item = ItemConfig.builder()
                    .itemCode(dto.getItemCode())
                    .itemName(dto.getItemName())
                    .category(category)
                    .uom(uom)
                    .unitCost(dto.getUnitCost())
                    .gstTaxRate(dto.getGstTaxRate())
                    .hsnSacCode(dto.getHsnSacCode())
                    .reorderLevel(dto.getReorderLevel())
                    .maxStockLevel(dto.getMaxStockLevel())
                    .minimumQty(dto.getMinimumQty())
                    .maximumQty(dto.getMaximumQty())
                    .description(dto.getDescription())
                    .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                    .build();

            ItemConfig savedItem = itemConfigRepository.save(item);
            return StandardResponse.success(convertToDTO(savedItem), "Item created successfully");
        } catch (Exception e) {
            log.error("Error creating item: ", e);
            return StandardResponse.error("Failed to create item", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    @Transactional
    public StandardResponse<ItemConfigDTO> updateItem(Long id, ItemConfigDTO dto) {
        try {
            ItemConfig item = itemConfigRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Item not found with ID: " + id));

            item.setItemCode(dto.getItemCode());
            item.setItemName(dto.getItemName());
            
            if (dto.getCategoryId() != null) {
                CommonMaster category = commonMasterRepository.findById(dto.getCategoryId())
                        .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + dto.getCategoryId()));
                item.setCategory(category);
            }

            if (dto.getUomId() != null) {
                CommonMaster uom = commonMasterRepository.findById(dto.getUomId())
                        .orElseThrow(() -> new ResourceNotFoundException("UOM not found with ID: " + dto.getUomId()));
                item.setUom(uom);
            }

            item.setUnitCost(dto.getUnitCost());
            item.setGstTaxRate(dto.getGstTaxRate());
            item.setHsnSacCode(dto.getHsnSacCode());
            item.setReorderLevel(dto.getReorderLevel());
            item.setMaxStockLevel(dto.getMaxStockLevel());
            item.setMinimumQty(dto.getMinimumQty());
            item.setMaximumQty(dto.getMaximumQty());
            item.setDescription(dto.getDescription());
            if (dto.getIsActive() != null) {
                item.setIsActive(dto.getIsActive());
            }

            ItemConfig updatedItem = itemConfigRepository.save(item);
            return StandardResponse.success(convertToDTO(updatedItem), "Item updated successfully");
        } catch (Exception e) {
            log.error("Error updating item: ", e);
            return StandardResponse.error("Failed to update item", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    public StandardResponse<ItemConfigDTO> getItemById(Long id) {
        try {
            ItemConfig item = itemConfigRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Item not found with ID: " + id));
            return StandardResponse.success(convertToDTO(item), "Item fetched successfully");
        } catch (Exception e) {
            log.error("Error fetching item: ", e);
            return StandardResponse.error("Failed to fetch item", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    public StandardResponse<List<ItemConfigDTO>> getAllItems() {
        try {
            List<ItemConfigDTO> dtos = itemConfigRepository.findAll().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return StandardResponse.success(dtos, "Items fetched successfully");
        } catch (Exception e) {
            log.error("Error fetching all items: ", e);
            return StandardResponse.error("Failed to fetch all items", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    @Transactional
    public StandardResponse<Void> deleteItem(Long id) {
        try {
            itemConfigRepository.deleteById(id);
            return StandardResponse.success(null, "Item deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting item: ", e);
            return StandardResponse.error("Failed to delete item", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    @Transactional
    public StandardResponse<ItemConfigDTO> updateItemStatus(Long id, Boolean isActive) {
        try {
            ItemConfig item = itemConfigRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Item not found with ID: " + id));
            item.setIsActive(isActive);
            ItemConfig updatedItem = itemConfigRepository.save(item);
            return StandardResponse.success(convertToDTO(updatedItem), "Item status updated successfully");
        } catch (Exception e) {
            log.error("Error updating item status: ", e);
            return StandardResponse.error("Failed to update item status", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    private ItemConfigDTO convertToDTO(ItemConfig item) {
        return ItemConfigDTO.builder()
                .id(item.getId())
                .itemCode(item.getItemCode())
                .itemName(item.getItemName())
                .categoryId(item.getCategory() != null ? item.getCategory().getId() : null)
                .categoryName(item.getCategory() != null ? item.getCategory().getValue() : null)
                .uomId(item.getUom() != null ? item.getUom().getId() : null)
                .uomName(item.getUom() != null ? item.getUom().getValue() : null)
                .unitCost(item.getUnitCost())
                .gstTaxRate(item.getGstTaxRate())
                .hsnSacCode(item.getHsnSacCode())
                .reorderLevel(item.getReorderLevel())
                .maxStockLevel(item.getMaxStockLevel())
                .minimumQty(item.getMinimumQty())
                .maximumQty(item.getMaximumQty())
                .description(item.getDescription())
                .isActive(item.getIsActive())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }
}
