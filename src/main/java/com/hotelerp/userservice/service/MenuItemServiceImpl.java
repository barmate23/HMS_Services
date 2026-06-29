package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.MenuItemDTO;
import com.hotelerp.userservice.entity.CommonMaster;
import com.hotelerp.userservice.entity.MenuItem;
import com.hotelerp.userservice.entity.Outlet;
import com.hotelerp.userservice.repository.CommonMasterRepository;
import com.hotelerp.userservice.repository.MenuItemRepository;
import com.hotelerp.userservice.repository.OutletRepository;
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
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final OutletRepository outletRepository;
    private final CommonMasterRepository commonMasterRepository;

    @Override
    @Transactional
    public StandardResponse<Void> createMenuItem(MenuItemDTO dto) {
        try {
            Long outletId = dto.getOutletId();
            if (outletId == null) throw new IllegalArgumentException("Outlet ID must not be null");
            
            Outlet outlet = outletRepository.findById(outletId)
                    .orElseThrow(() -> new ResourceNotFoundException("Outlet not found with ID: " + outletId));

            CommonMaster category = null;
            if (dto.getCategoryId() != null) {
                category = commonMasterRepository.findById(dto.getCategoryId())
                        .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + dto.getCategoryId()));
            }

            CommonMaster subcategory = null;
            if (dto.getSubcategoryId() != null) {
                subcategory = commonMasterRepository.findById(dto.getSubcategoryId())
                        .orElseThrow(() -> new ResourceNotFoundException("Subcategory not found with ID: " + dto.getSubcategoryId()));
            }

            MenuItem item = MenuItem.builder()
                    .outlet(outlet)
                    .itemName(dto.getItemName())
                    .category(category)
                    .subcategory(subcategory)
                    .itemImage(dto.getItemImage())
                    .price(dto.getPrice())
                    .taxPercent(dto.getTaxPercent())
                    .variants(dto.getVariants())
                    .modifiers(dto.getModifiers())
                    .happyHourPrice(dto.getHappyHourPrice())
                    .happyHourWindow(dto.getHappyHourWindow())
                    .linkedStockItem(dto.getLinkedStockItem())
                    .isAvailable(dto.getIsAvailable() != null ? dto.getIsAvailable() : true)
                    .isFeatured(dto.getIsFeatured() != null ? dto.getIsFeatured() : false)
                    .build();

            menuItemRepository.save(item);
            return StandardResponse.success("Menu item created successfully");
        } catch (ResourceNotFoundException e) {
            return StandardResponse.error(e.getMessage(), "RESOURCE_NOT_FOUND", e.getMessage());
        } catch (Exception e) {
            log.error("Error creating menu item: ", e);
            return StandardResponse.error("Failed to create menu item", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    @Transactional
    public StandardResponse<MenuItemDTO> updateMenuItem(Long id, MenuItemDTO dto) {
        try {
            MenuItem item = menuItemRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with ID: " + id));

            if (dto.getOutletId() != null) {
                Outlet outlet = outletRepository.findById(dto.getOutletId())
                        .orElseThrow(() -> new ResourceNotFoundException("Outlet not found with ID: " + dto.getOutletId()));
                item.setOutlet(outlet);
            }

            item.setItemName(dto.getItemName());
            if (dto.getCategoryId() != null) {
                CommonMaster category = commonMasterRepository.findById(dto.getCategoryId())
                        .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + dto.getCategoryId()));
                item.setCategory(category);
            }

            if (dto.getSubcategoryId() != null) {
                CommonMaster subcategory = commonMasterRepository.findById(dto.getSubcategoryId())
                        .orElseThrow(() -> new ResourceNotFoundException("Subcategory not found with ID: " + dto.getSubcategoryId()));
                item.setSubcategory(subcategory);
            }
            item.setItemImage(dto.getItemImage());
            item.setPrice(dto.getPrice());
            item.setTaxPercent(dto.getTaxPercent());
            item.setVariants(dto.getVariants());
            item.setModifiers(dto.getModifiers());
            item.setHappyHourPrice(dto.getHappyHourPrice());
            item.setHappyHourWindow(dto.getHappyHourWindow());
            item.setLinkedStockItem(dto.getLinkedStockItem());
            if (dto.getIsAvailable() != null) {
                item.setIsAvailable(dto.getIsAvailable());
            }
            if (dto.getIsFeatured() != null) {
                item.setIsFeatured(dto.getIsFeatured());
            }

            MenuItem updatedItem = menuItemRepository.save(item);
            return StandardResponse.success(convertToDTO(updatedItem), "Menu item updated successfully");
        } catch (ResourceNotFoundException e) {
            return StandardResponse.error(e.getMessage(), "RESOURCE_NOT_FOUND", e.getMessage());
        } catch (Exception e) {
            log.error("Error updating menu item: ", e);
            return StandardResponse.error("Failed to update menu item", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    public StandardResponse<MenuItemDTO> getMenuItemById(Long id) {
        try {
            MenuItem item = menuItemRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with ID: " + id));
            return StandardResponse.success(convertToDTO(item), "Menu item fetched successfully");
        } catch (ResourceNotFoundException e) {
            return StandardResponse.error(e.getMessage(), "RESOURCE_NOT_FOUND", e.getMessage());
        } catch (Exception e) {
            log.error("Error fetching menu item: ", e);
            return StandardResponse.error("Failed to fetch menu item", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    public StandardResponse<List<MenuItemDTO>> getMenuItemsByOutlet(Long outletId) {
        try {
            List<MenuItemDTO> dtos = menuItemRepository.findByOutletId(outletId).stream()
                    .filter(i -> !Boolean.TRUE.equals(i.getIsDeleted()))
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return StandardResponse.success(dtos, "Menu items fetched successfully");
        } catch (Exception e) {
            log.error("Error fetching menu items by outlet: ", e);
            return StandardResponse.error("Failed to fetch menu items", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    public StandardResponse<List<MenuItemDTO>> getAllMenuItems() {
        try {
            List<MenuItemDTO> dtos = menuItemRepository.findAll().stream()
                    .filter(i -> !Boolean.TRUE.equals(i.getIsDeleted()))
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return StandardResponse.success(dtos, "Menu items fetched successfully");
        } catch (Exception e) {
            log.error("Error fetching all menu items: ", e);
            return StandardResponse.error("Failed to fetch all menu items", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    public StandardResponse<List<MenuItemDTO>> getMenuItemsByFilter(Long categoryId, Long subcategoryId) {
        try {
            List<MenuItem> items;
            if (categoryId != null && subcategoryId != null) {
                items = menuItemRepository.findByCategoryIdAndSubcategoryIdAndIsDeletedFalse(categoryId, subcategoryId);
            } else if (categoryId != null) {
                items = menuItemRepository.findByCategoryIdAndIsDeletedFalse(categoryId);
            } else if (subcategoryId != null) {
                items = menuItemRepository.findBySubcategoryIdAndIsDeletedFalse(subcategoryId);
            } else {
                items = menuItemRepository.findByIsDeletedFalse();
            }
            List<MenuItemDTO> dtos = items.stream().map(this::convertToDTO).collect(Collectors.toList());
            return StandardResponse.success(dtos, "Menu items fetched successfully");
        } catch (Exception e) {
            log.error("Error fetching menu items by filter: ", e);
            return StandardResponse.error("Failed to fetch menu items", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    @Transactional
    public StandardResponse<Void> deleteMenuItem(Long id) {
        try {
            MenuItem item = menuItemRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with ID: " + id));
            item.setIsDeleted(true);
            menuItemRepository.save(item);
            return StandardResponse.success("Menu item deleted successfully");
        } catch (ResourceNotFoundException e) {
            return StandardResponse.error(e.getMessage(), "RESOURCE_NOT_FOUND", e.getMessage());
        } catch (Exception e) {
            log.error("Error deleting menu item: ", e);
            return StandardResponse.error("Failed to delete menu item", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    private MenuItemDTO convertToDTO(MenuItem item) {
        return MenuItemDTO.builder()
                .id(item.getId())
                .outletId(item.getOutlet().getId())
                .outletName(item.getOutlet().getName())
                .itemName(item.getItemName())
                .categoryId(item.getCategory() != null ? item.getCategory().getId() : null)
                .categoryName(item.getCategory() != null ? item.getCategory().getValue() : null)
                .subcategoryId(item.getSubcategory() != null ? item.getSubcategory().getId() : null)
                .subcategoryName(item.getSubcategory() != null ? item.getSubcategory().getValue() : null)
                .itemImage(item.getItemImage())
                .price(item.getPrice())
                .taxPercent(item.getTaxPercent())
                .variants(item.getVariants())
                .modifiers(item.getModifiers())
                .happyHourPrice(item.getHappyHourPrice())
                .happyHourWindow(item.getHappyHourWindow())
                .linkedStockItem(item.getLinkedStockItem())
                .isAvailable(item.getIsAvailable())
                .isFeatured(item.getIsFeatured())
                .build();
    }
}
