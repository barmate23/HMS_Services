package com.hotelerp.userservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a POS Bill (settlement record) for a POS Order.
 *
 * Billing Screen fields:
 *  - order          → the originating PosOrder
 *  - paymentMethod  → CommonMaster (category = PAYMENT_MODE): CASH, CARD, UPI, ROOM_CHARGE
 *  - status         → CommonMaster (category = BILL_STATUS): OPEN, SETTLED, VOID
 *  - compVoidReason → CommonMaster (category = COMP_VOID_REASON)
 *  - discount       → discount amount applied on the bill
 *  - paidAmount     → amount actually paid by guest
 *  - postToFolio    → if true, charge is posted to the room's active folio
 *  - folioPosting   → reference to the FolioPosting created (nullable)
 *  - billNumber     → auto-generated sequential bill number
 */
@Entity
@Table(name = "pos_bills")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PosBill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The POS order this bill settles (one-to-one per order) */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private PosOrder order;

    /** PAYMENT_MODE CommonMaster: CASH, CARD, UPI, ROOM_CHARGE, COMPLIMENTARY */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_method_id")
    private CommonMaster paymentMethod;

    /** BILL_STATUS CommonMaster: OPEN, SETTLED, VOID */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private CommonMaster status;

    /** COMP_VOID_REASON CommonMaster – populated when bill is VOID or COMPLIMENTARY */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comp_void_reason_id")
    private CommonMaster compVoidReason;

    /** Auto-generated, e.g. "BILL-0001" */
    @Column(name = "bill_number", length = 50, unique = true)
    private String billNumber;

    /** Gross amount (copied from PosOrder.totalAmount) */
    @Builder.Default
    @Column(name = "gross_amount", precision = 10, scale = 2)
    private BigDecimal grossAmount = BigDecimal.ZERO;

    /** Discount applied on this bill */
    @Builder.Default
    @Column(name = "discount", precision = 10, scale = 2)
    private BigDecimal discount = BigDecimal.ZERO;

    /** Net amount = grossAmount - discount */
    @Builder.Default
    @Column(name = "net_amount", precision = 10, scale = 2)
    private BigDecimal netAmount = BigDecimal.ZERO;

    /** Amount actually paid / tendered by the guest */
    @Builder.Default
    @Column(name = "paid_amount", precision = 10, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;

    /**
     * If true, the net amount is posted to the active folio of the room
     * linked to the POS order (Room Service orders).
     */
    @Builder.Default
    @Column(name = "post_to_folio")
    private Boolean postToFolio = false;

    /**
     * FK to FolioPosting created when postToFolio = true.
     * NULL for non-folio bills.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folio_posting_id")
    private FolioPosting folioPosting;

    @Builder.Default
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (grossAmount == null) grossAmount = BigDecimal.ZERO;
        if (discount == null) discount = BigDecimal.ZERO;
        if (netAmount == null) netAmount = BigDecimal.ZERO;
        if (paidAmount == null) paidAmount = BigDecimal.ZERO;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
