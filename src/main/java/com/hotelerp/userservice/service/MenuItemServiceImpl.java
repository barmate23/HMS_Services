package com.hotelerp.userservice.service;

import com.hotelerp.userservice.dto.MenuItemDTO;
import com.hotelerp.userservice.entity.MenuItem;
import com.hotelerp.userservice.entity.Outlet;
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

    @Override
    @Transactional
    public MenuItemDTO createMenuItem(MenuItemDTO dto) {
        Outlet outlet = outletRepository.findById(dto.getOutletId())
                .orElseThrow(() -> new RuntimeException("Outlet not found"));

        MenuItem item = MenuItem.builder()
                .outlet(outlet)
                .itemName(dto.getItemName())
                .category(dto.getCategory())
                .subcategory(dto.getSubcategory())
                .imageUrl(dto.getImageUrl())
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
        item.setCategory(dto.getCategory());
        item.setSubcategory(dto.getSubcategory());
        item.setImageUrl(dto.getImageUrl());
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
                .category(item.getCategory())
                .subcategory(item.getSubcategory())
                .imageUrl(item.getImageUrl())
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
