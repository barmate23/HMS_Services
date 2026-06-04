package com.hotelerp.userservice.controller;

import com.hotelerp.userservice.dto.MenuItemDTO;
import com.hotelerp.userservice.service.MenuItemService;
import com.hotelerp.userservice.constant.ServiceConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hmsService/v1/pos/menu")
@RequiredArgsConstructor
public class MenuItemController {

    private final MenuItemService menuItemService;

    @PostMapping(ServiceConstant.CREATE_MENU)
    public ResponseEntity<Void> createMenuItem(@RequestBody MenuItemDTO dto) {
        menuItemService.createMenuItem(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping(ServiceConstant.GET_MENU_BY_ID)
    public ResponseEntity<MenuItemDTO> getMenuItemById(@PathVariable Long id) {
        return ResponseEntity.ok(menuItemService.getMenuItemById(id));
    }

    @GetMapping(ServiceConstant.GET_ALL_MENU)
    public ResponseEntity<List<MenuItemDTO>> getAllMenuItems(@RequestParam(required = false) Long outletId) {
        if (outletId != null) {
            return ResponseEntity.ok(menuItemService.getMenuItemsByOutlet(outletId));
        }
        return ResponseEntity.ok(menuItemService.getAllMenuItems());
    }

    @PutMapping(ServiceConstant.UPDATE_MENU)
    public ResponseEntity<MenuItemDTO> updateMenuItem(@PathVariable Long id, @RequestBody MenuItemDTO dto) {
        return ResponseEntity.ok(menuItemService.updateMenuItem(id, dto));
    }

    @DeleteMapping(ServiceConstant.DELETE_MENU)
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long id) {
        menuItemService.deleteMenuItem(id);
        return ResponseEntity.noContent().build();
    }
}
