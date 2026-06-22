package com.hotelerp.userservice.controller;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.InventoryStockDTO;
import com.hotelerp.userservice.service.InventoryStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hmsService/v1/inventory/stocks")
@RequiredArgsConstructor
public class InventoryStockController {

    private final InventoryStockService inventoryStockService;

    @PostMapping("/createStockItem")
    public ResponseEntity<StandardResponse<InventoryStockDTO>> createStockItem(@RequestBody InventoryStockDTO dto) {
        return ResponseEntity.ok(inventoryStockService.createStockItem(dto));
    }

    @PutMapping("/updateStockItem/{id}")
    public ResponseEntity<StandardResponse<InventoryStockDTO>> updateStockItem(@PathVariable Long id, @RequestBody InventoryStockDTO dto) {
        return ResponseEntity.ok(inventoryStockService.updateStockItem(id, dto));
    }

    @GetMapping("/getStockItemById/{id}")
    public ResponseEntity<StandardResponse<InventoryStockDTO>> getStockItemById(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryStockService.getStockItemById(id));
    }

    @GetMapping("/getAllStockItems")
    public ResponseEntity<StandardResponse<List<InventoryStockDTO>>> getAllStockItems() {
        return ResponseEntity.ok(inventoryStockService.getAllStockItems());
    }

    @DeleteMapping("/deleteStockItem/{id}")
    public ResponseEntity<StandardResponse<Void>> deleteStockItem(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryStockService.deleteStockItem(id));
    }

    @PostMapping("/updateStockStatus")
    public ResponseEntity<StandardResponse<InventoryStockDTO>> updateStockStatus(@RequestParam Long id, @RequestParam Long statusId) {
        return ResponseEntity.ok(inventoryStockService.updateStockStatus(id, statusId));
    }
}
