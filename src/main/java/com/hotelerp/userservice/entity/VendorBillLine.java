package com.hotelerp.userservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vendor_bill_lines")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorBillLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_bill_id", nullable = false)
    @JsonIgnore
    private VendorBill vendorBill;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private ItemConfig item;

    @Column(name = "received_quantity", precision = 19, scale = 2)
    private BigDecimal receivedQuantity;

    @Column(name = "rate", precision = 19, scale = 2)
    private BigDecimal rate;

    @Column(name = "total_amount", precision = 19, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Builder.Default
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isDeleted == null) isDeleted = false;
    }
}
