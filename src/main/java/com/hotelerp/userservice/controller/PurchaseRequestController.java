package com.hotelerp.userservice.controller;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.PurchaseRequestDTO;
import com.hotelerp.userservice.service.PurchaseRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory/purchase-requests")
@RequiredArgsConstructor
public class PurchaseRequestController {

    private final PurchaseRequestService purchaseRequestService;

    @PostMapping
    public ResponseEntity<StandardResponse<PurchaseRequestDTO>> create(@RequestBody PurchaseRequestDTO dto) {
        return ResponseEntity.ok(purchaseRequestService.createPurchaseRequest(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StandardResponse<PurchaseRequestDTO>> update(@PathVariable Long id, @RequestBody PurchaseRequestDTO dto) {
        return ResponseEntity.ok(purchaseRequestService.updatePurchaseRequest(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse<PurchaseRequestDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseRequestService.getPurchaseRequestById(id));
    }

    @GetMapping
    public ResponseEntity<StandardResponse<List<PurchaseRequestDTO>>> getAll() {
        return ResponseEntity.ok(purchaseRequestService.getAllPurchaseRequests());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<StandardResponse<Void>> delete(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseRequestService.deletePurchaseRequest(id));
    }

    @PatchMapping("/{id}/status/{statusId}")
    public ResponseEntity<StandardResponse<PurchaseRequestDTO>> updateStatus(@PathVariable Long id, @PathVariable Long statusId) {
        return ResponseEntity.ok(purchaseRequestService.updateStatus(id, statusId));
    }
}
