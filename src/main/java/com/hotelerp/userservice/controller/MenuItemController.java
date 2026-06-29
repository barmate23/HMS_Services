package com.hotelerp.userservice.controller;

import com.hotelerp.userservice.common.StandardResponse;
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
    public ResponseEntity<StandardResponse<Void>> createMenuItem(@RequestBody MenuItemDTO dto) {
        StandardResponse<Void> response = menuItemService.createMenuItem(dto);
        HttpStatus status = response.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping(ServiceConstant.GET_MENU_BY_ID)
    public ResponseEntity<StandardResponse<MenuItemDTO>> getMenuItemById(@PathVariable Long id) {
        StandardResponse<MenuItemDTO> response = menuItemService.getMenuItemById(id);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping(ServiceConstant.GET_ALL_MENU)
    public ResponseEntity<StandardResponse<List<MenuItemDTO>>> getAllMenuItems(
            @RequestParam(required = false) Long outletId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long subcategoryId) {
        StandardResponse<List<MenuItemDTO>> response;
        if (categoryId != null || subcategoryId != null) {
            response = menuItemService.getMenuItemsByFilter(categoryId, subcategoryId);
        } else if (outletId != null) {
            response = menuItemService.getMenuItemsByOutlet(outletId);
        } else {
            response = menuItemService.getAllMenuItems();
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping(ServiceConstant.UPDATE_MENU)
    public ResponseEntity<StandardResponse<MenuItemDTO>> updateMenuItem(@PathVariable Long id, @RequestBody MenuItemDTO dto) {
        StandardResponse<MenuItemDTO> response = menuItemService.updateMenuItem(id, dto);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @DeleteMapping(ServiceConstant.DELETE_MENU)
    public ResponseEntity<StandardResponse<Void>> deleteMenuItem(@PathVariable Long id) {
        StandardResponse<Void> response = menuItemService.deleteMenuItem(id);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
}
