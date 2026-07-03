package com.hotelerp.userservice.controller;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.billing.InvoiceDTO;
import com.hotelerp.userservice.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hmsService/v1/billing/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping("/generateInvoice/{folioId}")
    public ResponseEntity<StandardResponse<InvoiceDTO>> generateInvoice(@PathVariable Long folioId) {
        StandardResponse<InvoiceDTO> response = invoiceService.generateInvoice(folioId);
        HttpStatus status = response.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/getAllInvoices")
    public ResponseEntity<StandardResponse<List<InvoiceDTO>>> getAllInvoices() {
        StandardResponse<List<InvoiceDTO>> response = invoiceService.getAllInvoices();
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/downloadInvoice/{id}")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Long id) {
        byte[] pdf = invoiceService.downloadInvoice(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
