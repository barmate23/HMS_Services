package com.hotelerp.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for POS Billing screen.
 *
 * List columns : BILL | ORDER | GUEST/ROOM | AMOUNT | DISCOUNT | PAYMENT | FOLIO | STATUS | ACTIONS
 * Create form  : ORDER, ORDER FROM, STATUS, TABLE, GUEST NAME,
 *                DISCOUNT, PAID AMOUNT, PAYMENT MODES, COMP/VOID REASON, POST TO FOLIO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PosBillDTO {

    // ── Identifiers ──────────────────────────────────────────────────────────
    private Long id;
    private String billNumber;

    // ── Order info ────────────────────────────────────────────────────────────
    private Long orderId;
    private String orderRef;          // e.g. "ORD-22"

    // "Order From" context
    private String orderFrom;         // TABLE | ROOM | TAKEAWAY
    private Long tableId;
    private String tableNumber;
    private Long roomId;
    private String roomNumber;
    private String guestName;

    // ── Amounts ───────────────────────────────────────────────────────────────
    private BigDecimal grossAmount;   // Total from order items
    private BigDecimal discount;      // Discount entered on bill
    private BigDecimal netAmount;     // grossAmount - discount
    private BigDecimal paidAmount;    // Actual amount paid / tendered

    // ── Payment mode ──────────────────────────────────────────────────────────
    private Long paymentMethodId;     // CommonMaster id (PAYMENT_MODE)
    private String paymentMethodName; // CASH | CARD | UPI | ROOM_CHARGE …

    // ── Bill status ───────────────────────────────────────────────────────────
    private Long statusId;            // CommonMaster id (BILL_STATUS)
    private String statusName;        // OPEN | SETTLED | VOID

    // ── Comp / Void reason ────────────────────────────────────────────────────
    private Long compVoidReasonId;    // CommonMaster id (COMP_VOID_REASON)
    private String compVoidReasonName;

    // ── Folio posting ─────────────────────────────────────────────────────────
    /** Whether to post net amount to the room's active folio */
    private Boolean postToFolio;

    /** Folio posting ID created (populated after create if postToFolio=true) */
    private Long folioPostingId;

    // ── Misc ──────────────────────────────────────────────────────────────────
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
