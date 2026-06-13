package com.hotelerp.userservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "laundry_price_master")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LaundryPriceMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category", nullable = false, length = 50)
    private String category;

    @Column(name = "item_name", nullable = false, length = 100)
    private String itemName;

    @Column(name = "wash_fold_price")
    private Double washFoldPrice;

    @Column(name = "wash_press_price")
    private Double washPressPrice;

    @Column(name = "dry_clean_price")
    private Double dryCleanPrice;

    @Column(name = "express_surcharge_percentage")
    private Double expressSurchargePercentage;

    @ElementCollection
    @CollectionTable(
            name = "laundry_price_master_service_rates",
            joinColumns = @JoinColumn(name = "price_master_id")
    )
    @MapKeyColumn(name = "service_name", length = 100)
    @Column(name = "price")
    @Builder.Default
    private Map<String, Double> servicePrices = new HashMap<>();

    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private String status = "ACTIVE";

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
        if (status == null) status = "ACTIVE";
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
