package com.hotelerp.userservice.controller;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.billing.*;
import com.hotelerp.userservice.service.FolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hmsService/v1/billing/folios")
@RequiredArgsConstructor
public class FolioController {

    private final FolioService folioService;

    @GetMapping("/getLedgerByFolioId/{folioId}")
    public ResponseEntity<StandardResponse<FolioLedgerDTO>> getLedger(@PathVariable Long folioId) {
        return ResponseEntity.ok(folioService.getLedger(folioId));
    }

    @PostMapping("/postCharge")
    public ResponseEntity<StandardResponse<Void>> postCharge(@RequestBody FolioPostingRequest request) {
        return ResponseEntity.ok(folioService.postCharge(request));
    }

    @PostMapping("/postToFolio")
    public ResponseEntity<StandardResponse<Void>> postToFolio(@RequestBody PostToFolioRequest request) {
        return ResponseEntity.ok(folioService.postToFolio(request));
    }

    @PostMapping("/collectFolioPayment")
    public ResponseEntity<StandardResponse<Void>> collectPayment(@RequestBody FolioPaymentRequest request) {
        return ResponseEntity.ok(folioService.collectPayment(request));
    }


    @GetMapping("/getActiveFolios")
    public ResponseEntity<StandardResponse<java.util.List<FolioLedgerDTO>>> getActiveFolios() {
        return ResponseEntity.ok(folioService.getActiveFolios());
    }

    @GetMapping("/getAllPayments")
    public ResponseEntity<StandardResponse<java.util.List<FolioPaymentDTO>>> getAllPayments() {
        return ResponseEntity.ok(folioService.getAllPayments());
    }
}
