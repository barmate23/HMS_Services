package com.hotelerp.userservice.service;

import com.hotelerp.userservice.dto.MenuItemDTO;
import com.hotelerp.userservice.entity.CommonMaster;
import com.hotelerp.userservice.entity.MenuItem;
import com.hotelerp.userservice.entity.Outlet;
import com.hotelerp.userservice.repository.CommonMasterRepository;
import com.hotelerp.userservice.repository.MenuItemRepository;
import com.hotelerp.userservice.repository.OutletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final OutletRepository outletRepository;
    private final CommonMasterRepository commonMasterRepository;

    @Override
    @Transactional
    public MenuItemDTO createMenuItem(MenuItemDTO dto) {
        Outlet outlet = outletRepository.findById(dto.getOutletId())
                .orElseThrow(() -> new RuntimeException("Outlet not found"));

        CommonMaster category = null;
        if (dto.getCategoryId() != null) {
            category = commonMasterRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
        }

        CommonMaster subcategory = null;
        if (dto.getSubcategoryId() != null) {
            subcategory = commonMasterRepository.findById(dto.getSubcategoryId())
                    .orElseThrow(() -> new RuntimeException("Subcategory not found"));
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

        return convertToDTO(menuItemRepository.save(item));
    }

    @Override
    @Transactional
    public MenuItemDTO updateMenuItem(Long id, MenuItemDTO dto) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));

        if (dto.getOutletId() != null) {
            Outlet outlet = outletRepository.findById(dto.getOutletId()).orElseThrow(() -> new RuntimeException("Outlet not found"));
            item.setOutlet(outlet);
        }

        item.setItemName(dto.getItemName());
        if (dto.getCategoryId() != null) {
            CommonMaster category = commonMasterRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            item.setCategory(category);
        }

        if (dto.getSubcategoryId() != null) {
            CommonMaster subcategory = commonMasterRepository.findById(dto.getSubcategoryId())
                    .orElseThrow(() -> new RuntimeException("Subcategory not found"));
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

        return convertToDTO(menuItemRepository.save(item));
    }

    @Override
    public MenuItemDTO getMenuItemById(Long id) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));
        return convertToDTO(item);
    }

    @Override
    public List<MenuItemDTO> getMenuItemsByOutlet(Long outletId) {
        return menuItemRepository.findByOutletId(outletId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MenuItemDTO> getAllMenuItems() {
        return menuItemRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteMenuItem(Long id) {
        menuItemRepository.deleteById(id);
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
