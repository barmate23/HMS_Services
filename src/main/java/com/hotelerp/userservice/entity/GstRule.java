package com.hotelerp.userservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "gst_rules", indexes = {
        @Index(name = "idx_gst_service_category", columnList = "service_category")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GstRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    /** Service category (fetched from CommonMaster, e.g. Room, Food, Laundry) */
    @Column(name = "service_category", nullable = false, length = 100)
    private String serviceCategory;

    /** HSN / SAC code (e.g. 9963, 9987) */
    @Column(name = "hsn_sac_code", nullable = false, length = 20)
    private String hsnSacCode;

    /** CGST rate in percentage */
    @Column(name = "cgst_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal cgstRate;

    /** SGST rate in percentage */
    @Column(name = "sgst_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal sgstRate;

    /** Calculated IGST = CGST + SGST (stored for quick queries) */
    @Column(name = "igst_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal igstRate;

    /** Optional description */
    @Column(name = "description", length = 500)
    private String description;

    /** Soft-delete flag */
    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        computeIgst();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        computeIgst();
    }

    private void computeIgst() {
        if (cgstRate != null && sgstRate != null) {
            igstRate = cgstRate.add(sgstRate);
        }
    }
}
