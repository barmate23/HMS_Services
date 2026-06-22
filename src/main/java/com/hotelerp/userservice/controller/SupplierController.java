package com.hotelerp.userservice.controller;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.SupplierDTO;
import com.hotelerp.userservice.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hmsService/v1/purchase/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping("/createSupplier")
    public ResponseEntity<StandardResponse<SupplierDTO>> create(@RequestBody SupplierDTO dto) {
        return ResponseEntity.ok(supplierService.createSupplier(dto));
    }

    @PutMapping("/updateSupplier/{id}")
    public ResponseEntity<StandardResponse<SupplierDTO>> update(@PathVariable Long id, @RequestBody SupplierDTO dto) {
        return ResponseEntity.ok(supplierService.updateSupplier(id, dto));
    }

    @GetMapping("/getBySupplierId/{id}")
    public ResponseEntity<StandardResponse<SupplierDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(supplierService.getSupplierById(id));
    }

    @GetMapping("/getAllSupplier")
    public ResponseEntity<StandardResponse<List<SupplierDTO>>> getAll() {
        return ResponseEntity.ok(supplierService.getAllSuppliers());
    }

    @DeleteMapping("/deleteSupplier/{id}")
    public ResponseEntity<StandardResponse<Void>> delete(@PathVariable Long id) {
        return ResponseEntity.ok(supplierService.deleteSupplier(id));
    }

    @PostMapping("/updateSupplierStatus")
    public ResponseEntity<StandardResponse<SupplierDTO>> updateStatus(@PathVariable Long id, @PathVariable Long statusId) {
        return ResponseEntity.ok(supplierService.updateStatus(id, statusId));
    }
}
