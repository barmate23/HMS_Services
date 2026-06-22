package com.hotelerp.userservice.controller;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.VendorBillDTO;
import com.hotelerp.userservice.service.VendorBillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hmsService/v1/purchase/vendor-bills")
@RequiredArgsConstructor
public class VendorController {

    private final VendorBillService vendorBillService;

    @PostMapping("/createVendorBill")
    public ResponseEntity<StandardResponse<VendorBillDTO>> create(@RequestBody VendorBillDTO dto) {
        return ResponseEntity.ok(vendorBillService.createVendorBill(dto));
    }

    @PutMapping("/updateVendorBill/{id}")
    public ResponseEntity<StandardResponse<VendorBillDTO>> update(@PathVariable Long id, @RequestBody VendorBillDTO dto) {
        return ResponseEntity.ok(vendorBillService.updateVendorBill(id, dto));
    }

    @GetMapping("/getByVendorBillId/{id}")
    public ResponseEntity<StandardResponse<VendorBillDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(vendorBillService.getVendorBillById(id));
    }

    @GetMapping("/getAllVendorBill")
    public ResponseEntity<StandardResponse<List<VendorBillDTO>>> getAll() {
        return ResponseEntity.ok(vendorBillService.getAllVendorBills());
    }

    @DeleteMapping("/deleteVendorBill")
    public ResponseEntity<StandardResponse<Void>> delete(@PathVariable Long id) {
        return ResponseEntity.ok(vendorBillService.deleteVendorBill(id));
    }

    @PatchMapping("/updateVendorBillStatus")
    public ResponseEntity<StandardResponse<VendorBillDTO>> updateStatus(@RequestParam Long id, @RequestParam Long statusId) {
        return ResponseEntity.ok(vendorBillService.updateStatus(id, statusId));
    }
}
