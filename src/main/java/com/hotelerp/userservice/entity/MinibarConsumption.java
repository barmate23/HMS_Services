package com.hotelerp.userservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "minibar_consumptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MinibarConsumption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private InventoryStock item;

    @Column(name = "par_level")
    private Integer parLevel;

    @Column(name = "current_qty")
    private Integer currentQty;

    @Column(name = "consumed_qty")
    private Integer consumedQty;

    @Column(name = "charge_amount", precision = 19, scale = 2)
    private BigDecimal chargeAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private CommonMaster status;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "consumption_date")
    private LocalDateTime consumptionDate;

    @Builder.Default
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (consumptionDate == null) consumptionDate = LocalDateTime.now();
        if (isDeleted == null) isDeleted = false;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
