package com.hotelerp.userservice.service;

import com.hotelerp.userservice.dto.MenuItemDTO;
import java.util.List;

public interface MenuItemService {
    MenuItemDTO createMenuItem(MenuItemDTO dto);
    MenuItemDTO updateMenuItem(Long id, MenuItemDTO dto);
    MenuItemDTO getMenuItemById(Long id);
    List<MenuItemDTO> getMenuItemsByOutlet(Long outletId);
    List<MenuItemDTO> getAllMenuItems();
    void deleteMenuItem(Long id);
}
