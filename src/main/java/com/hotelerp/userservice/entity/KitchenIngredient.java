package com.hotelerp.userservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "kitchen_ingredients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KitchenIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Auto-generated code: ING-001, ING-002, … */
    @Column(name = "ingredient_code", nullable = false, unique = true, length = 20)
    private String ingredientCode;

    @Column(name = "ingredient_name", nullable = false, length = 150)
    private String ingredientName;

    /**
     * Category from CommonMaster (category = "INGREDIENT_CATEGORY")
     * e.g. DAIRY, PRODUCE, SPICES & CONDIMENTS, POULTRY & MEAT …
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CommonMaster category;

    /**
     * Base Unit (recipe usage) from CommonMaster (category = "UNIT_OF_MEASURE")
     * e.g. GRAM, ML, KG …
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "base_unit_id")
    private CommonMaster baseUnit;

    /**
     * Purchase Unit (procurement) from CommonMaster (category = "UNIT_OF_MEASURE")
     * e.g. LITER, KG …
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_unit_id")
    private CommonMaster purchaseUnit;

    /** How many base units fit in 1 purchase unit (e.g. 1 LITER = 1000 GRAM → factor 1000) */
    @Column(name = "purchase_conversion_factor", precision = 14, scale = 4)
    private BigDecimal purchaseConversionFactor;

    /** Usable yield % after prep/trimming (0-100) */
    @Column(name = "usable_yield_percent", precision = 5, scale = 2)
    private BigDecimal usableYieldPercent;

    /** Cost per purchase unit (₹/purchase unit) */
    @Column(name = "cost_per_purchase_unit", precision = 14, scale = 4)
    private BigDecimal costPerPurchaseUnit;

    /** Current stock level expressed in base unit */
    @Column(name = "current_stock_level", precision = 14, scale = 4)
    private BigDecimal currentStockLevel;

    /** Reorder threshold level in base unit */
    @Column(name = "reorder_threshold_level", precision = 14, scale = 4)
    private BigDecimal reorderThresholdLevel;

    /** Reorder quantity in base unit */
    @Column(name = "reorder_quantity", precision = 14, scale = 4)
    private BigDecimal reorderQuantity;

    /**
     * Storage type from CommonMaster (category = "STORAGE_TYPE")
     * e.g. DRY_STORE, CHILLED, FROZEN …
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storage_type_id")
    private CommonMaster storageType;

    @Column(name = "preferred_supplier", length = 200)
    private String preferredSupplier;

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
        if (isDeleted == null) isDeleted = false;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
