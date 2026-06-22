package com.hotelerp.userservice.controller;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.billing.*;
import com.hotelerp.userservice.service.FolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/billing/folios")
@RequiredArgsConstructor
public class FolioController {

    private final FolioService folioService;

    @GetMapping("/{folioId}/ledger")
    public ResponseEntity<StandardResponse<FolioLedgerDTO>> getLedger(@PathVariable Long folioId) {
        return ResponseEntity.ok(folioService.getLedger(folioId));
    }

    @PostMapping("/charges")
    public ResponseEntity<StandardResponse<Void>> postCharge(@RequestBody FolioPostingRequest request) {
        return ResponseEntity.ok(folioService.postCharge(request));
    }

    @PostMapping("/payments")
    public ResponseEntity<StandardResponse<Void>> collectPayment(@RequestBody FolioPaymentRequest request) {
        return ResponseEntity.ok(folioService.collectPayment(request));
    }

    @PostMapping("/{folioId}/settle")
    public ResponseEntity<StandardResponse<Void>> settleFolio(@PathVariable Long folioId) {
        return ResponseEntity.ok(folioService.settledFolio(folioId));
    }

    @GetMapping("/active")
    public ResponseEntity<StandardResponse<java.util.List<FolioLedgerDTO>>> getActiveFolios() {
        return ResponseEntity.ok(folioService.getActiveFolios());
    }
}
