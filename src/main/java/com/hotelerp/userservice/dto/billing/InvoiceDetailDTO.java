package com.hotelerp.userservice.dto.billing;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Complete response DTO for the Tax Invoice PDF.
 * Every field maps to a visible section in the invoice prototype.
 */
@Data
@Builder
public class InvoiceDetailDTO {

    // ── Invoice Header ────────────────────────────────────────────────────
    private String invoiceNumber;       // e.g. INV-2026-10492
    private LocalDateTime invoiceDate;  // Issued date
    private String folioNumber;         // e.g. FOL-1001-A
    private String invoiceStatus;       // DRAFT / ISSUED / PAID

    // ── Hotel / Company Info (static) ─────────────────────────────────────
    private HotelInfoDTO hotelInfo;

    // ── Billing To (Guest / Company) ──────────────────────────────────────
    private BillingToDTO billingTo;

    // ── Stay Records ──────────────────────────────────────────────────────
    private StayRecordDTO stayRecord;

    // ── Service Line Items ────────────────────────────────────────────────
    private List<LineItemDTO> lineItems;

    // ── GST Breakdown ─────────────────────────────────────────────────────
    private List<GstBreakdownDTO> gstBreakdown;

    // ── Summary / Totals ─────────────────────────────────────────────────
    private BigDecimal netTaxableBase;   // Sum of base (charge) amounts
    private BigDecimal cgstSubtotal;     // Half of total tax
    private BigDecimal sgstSubtotal;     // Half of total tax
    private BigDecimal totalTax;         // CGST + SGST
    private BigDecimal grandTotal;       // netTaxableBase + totalTax
    private BigDecimal paymentsReceived; // Total payments / advance paid
    private BigDecimal balanceDue;       // grandTotal - paymentsReceived

    // ═══════════════════════════════════════════════════════════════════════
    // Inner DTOs
    // ═══════════════════════════════════════════════════════════════════════

    @Data
    @Builder
    public static class HotelInfoDTO {
        private String name;        // HMS CLOUD HOTELS & RESORTS
        private String address;     // 123 Hospitality Way, ...
        private String gstin;
        private String pan;
        private String email;
        private String tel;
    }

    @Data
    @Builder
    public static class BillingToDTO {
        private String guestName;       // Akshay Barmate
        private String addressLine1;
        private String addressLine2;
        private String city;
        private String state;
        private String postCode;
        private String placeOfSupply;   // e.g. Uttar Pradesh (State Code: 09)
        // Corporate billing (optional)
        private String organisationName;
        private String gstNumber;
    }

    @Data
    @Builder
    public static class StayRecordDTO {
        private String roomNumber;      // e.g. 302
        private String roomTypeName;    // e.g. Executive Suite
        private LocalDateTime checkInDateTime;
        private LocalDateTime checkOutDateTime;
        private Integer numberOfNights;
        private Integer numberOfAdults;
        private Integer numberOfChildren;
    }

    @Data
    @Builder
    public static class LineItemDTO {
        private Integer srNo;
        private LocalDateTime date;     // posting date
        private String sacCode;         // mapped from source type
        private String serviceTitle;    // e.g. Room Accommodation Charges
        private String serviceDescription; // detail line
        private BigDecimal baseValue;   // chargeAmount
        private BigDecimal taxAmount;
        private BigDecimal totalAmount;
    }

    @Data
    @Builder
    public static class GstBreakdownDTO {
        private String sacCode;
        private String category;        // e.g. Room, F&B, Laundry
        private BigDecimal taxableAmount;
        private BigDecimal cgstRate;    // percentage e.g. 6.0
        private BigDecimal cgstAmount;
        private BigDecimal sgstRate;    // percentage e.g. 6.0
        private BigDecimal sgstAmount;
        private BigDecimal totalTax;
    }
}
