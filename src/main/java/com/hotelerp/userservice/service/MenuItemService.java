package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.MenuItemDTO;
import java.util.List;

public interface MenuItemService {
    StandardResponse<Void> createMenuItem(MenuItemDTO dto);
    StandardResponse<MenuItemDTO> updateMenuItem(Long id, MenuItemDTO dto);
    StandardResponse<MenuItemDTO> getMenuItemById(Long id);
    StandardResponse<List<MenuItemDTO>> getMenuItemsByOutlet(Long outletId);
    StandardResponse<List<MenuItemDTO>> getAllMenuItems();
    StandardResponse<List<MenuItemDTO>> getMenuItemsByFilter(Long categoryId, Long subcategoryId);
    StandardResponse<Void> deleteMenuItem(Long id);
}
