package com.hotelerp.userservice.controller;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.PurchaseOrderDTO;
import com.hotelerp.userservice.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hmsService/v1/purchase/orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @PostMapping("/createPurchaseOrder")
    public ResponseEntity<StandardResponse<PurchaseOrderDTO>> create(@RequestBody PurchaseOrderDTO dto) {
        return ResponseEntity.ok(purchaseOrderService.createPurchaseOrder(dto));
    }

    @PutMapping("/updatePurchaseOrder/{id}")
    public ResponseEntity<StandardResponse<PurchaseOrderDTO>> update(@PathVariable Long id, @RequestBody PurchaseOrderDTO dto) {
        return ResponseEntity.ok(purchaseOrderService.updatePurchaseOrder(id, dto));
    }

    @GetMapping("/getPurchaseOrderById/{id}")
    public ResponseEntity<StandardResponse<PurchaseOrderDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseOrderService.getPurchaseOrderById(id));
    }

    @GetMapping("/getAllPurchaseOrder")
    public ResponseEntity<StandardResponse<List<PurchaseOrderDTO>>> getAll() {
        return ResponseEntity.ok(purchaseOrderService.getAllPurchaseOrders());
    }

    @DeleteMapping("/deletePurchaseOrder/{id}")
    public ResponseEntity<StandardResponse<Void>> delete(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseOrderService.deletePurchaseOrder(id));
    }

    @PatchMapping("/updatePurchaseOrderStatus")
    public ResponseEntity<StandardResponse<PurchaseOrderDTO>> updateStatus(@RequestParam Long id, @RequestParam Long statusId) {
        return ResponseEntity.ok(purchaseOrderService.updateStatus(id, statusId));
    }
}
