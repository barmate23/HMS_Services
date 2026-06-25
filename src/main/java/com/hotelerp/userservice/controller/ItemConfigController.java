package com.hotelerp.userservice.controller;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.ItemConfigDTO;
import com.hotelerp.userservice.service.ItemConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hmsService/v1/inventory/item-configs")
@RequiredArgsConstructor
public class ItemConfigController {

    private final ItemConfigService itemConfigService;

    @PostMapping("/createItem")
    public ResponseEntity<StandardResponse<ItemConfigDTO>> createItem(@RequestBody ItemConfigDTO dto) {
        return ResponseEntity.ok(itemConfigService.createItem(dto));
    }

    @PutMapping("/updateItem/{id}")
    public ResponseEntity<StandardResponse<ItemConfigDTO>> updateItem(@PathVariable Long id, @RequestBody ItemConfigDTO dto) {
        return ResponseEntity.ok(itemConfigService.updateItem(id, dto));
    }

    @GetMapping("/getItemById/{id}")
    public ResponseEntity<StandardResponse<ItemConfigDTO>> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(itemConfigService.getItemById(id));
    }

    @GetMapping("/getAllItems")
    public ResponseEntity<StandardResponse<List<ItemConfigDTO>>> getAllItems() {
        return ResponseEntity.ok(itemConfigService.getAllItems());
    }

    @DeleteMapping("/deleteItem/{id}")
    public ResponseEntity<StandardResponse<Void>> deleteItem(@PathVariable Long id) {
        return ResponseEntity.ok(itemConfigService.deleteItem(id));
    }

    @PatchMapping("/updateItemStatus/{id}")
    public ResponseEntity<StandardResponse<ItemConfigDTO>> updateItemStatus(@PathVariable Long id, @RequestParam Boolean isActive) {
        return ResponseEntity.ok(itemConfigService.updateItemStatus(id, isActive));
    }
}
