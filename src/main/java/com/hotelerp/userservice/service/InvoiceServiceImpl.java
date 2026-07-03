package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.billing.InvoiceDTO;
import com.hotelerp.userservice.entity.Folio;
import com.hotelerp.userservice.entity.Invoice;
import com.hotelerp.userservice.repository.FolioRepository;
import com.hotelerp.userservice.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final FolioRepository folioRepository;

    @Override
    public StandardResponse<InvoiceDTO> generateInvoice(Long folioId) {
        try {
            Folio folio = folioRepository.findById(folioId)
                    .orElseThrow(() -> new RuntimeException("Folio not found"));

            Invoice invoice = Invoice.builder()
                    .folio(folio)
                    .invoiceNumber("INV-2026-" + (1000 + folioId))
                    .status("PAID")
                    .issuedAt(LocalDateTime.now())
                    .totalAmount(folio.getTotalCharges())
                    .taxAmount(folio.getTaxAmount())
                    .build();

            invoice = invoiceRepository.save(invoice);

            return StandardResponse.success(mapToDTO(invoice), "Invoice generated successfully");
        } catch (Exception e) {
            return StandardResponse.error(e.getMessage(), "INVOICE_GENERATE_ERROR", e.getMessage());
        }
    }

    @Override
    public StandardResponse<List<InvoiceDTO>> getAllInvoices() {
        try {
            List<InvoiceDTO> invoices = invoiceRepository.findAll().stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
            return StandardResponse.success(invoices, "Invoices fetched successfully");
        } catch (Exception e) {
            return StandardResponse.error(e.getMessage(), "INVOICE_FETCH_ERROR", e.getMessage());
        }
    }

    @Override
    public byte[] downloadInvoice(Long invoiceId) {
        // Mock PDF generation logic
        return "Mock Invoice PDF Content".getBytes();
    }

    private InvoiceDTO mapToDTO(Invoice invoice) {
        Folio folio = invoice.getFolio();
        String guestName = "Unknown";
        if (folio != null && folio.getReservation() != null && folio.getReservation().getGuest() != null) {
            guestName = folio.getReservation().getGuest().getFirstName() + " " + folio.getReservation().getGuest().getLastName();
        }

        return InvoiceDTO.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .folioNumber(folio != null ? folio.getFolioNumber() : "")
                .guestName(guestName)
                .amount(invoice.getTotalAmount())
                .status(invoice.getStatus())
                .date(invoice.getIssuedAt())
                .build();
    }
}
