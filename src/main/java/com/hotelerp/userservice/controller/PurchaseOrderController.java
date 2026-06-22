package com.hotelerp.userservice.controller;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.PurchaseOrderDTO;
import com.hotelerp.userservice.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase/orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @PostMapping
    public ResponseEntity<StandardResponse<PurchaseOrderDTO>> create(@RequestBody PurchaseOrderDTO dto) {
        return ResponseEntity.ok(purchaseOrderService.createPurchaseOrder(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StandardResponse<PurchaseOrderDTO>> update(@PathVariable Long id, @RequestBody PurchaseOrderDTO dto) {
        return ResponseEntity.ok(purchaseOrderService.updatePurchaseOrder(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse<PurchaseOrderDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseOrderService.getPurchaseOrderById(id));
    }

    @GetMapping
    public ResponseEntity<StandardResponse<List<PurchaseOrderDTO>>> getAll() {
        return ResponseEntity.ok(purchaseOrderService.getAllPurchaseOrders());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<StandardResponse<Void>> delete(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseOrderService.deletePurchaseOrder(id));
    }

    @PatchMapping("/{id}/status/{statusId}")
    public ResponseEntity<StandardResponse<PurchaseOrderDTO>> updateStatus(@PathVariable Long id, @PathVariable Long statusId) {
        return ResponseEntity.ok(purchaseOrderService.updateStatus(id, statusId));
    }
}
