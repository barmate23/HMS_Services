package com.hotelerp.userservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

/**
 * One BOM line in a recipe:
 *   ingredient  + netQty + prepWaste%  →  grossQty  →  lineCost
 *
 * Calculations (stored for quick reads):
 *   grossQty = netQty / (1 - prepWastePercent / 100)
 *   lineCost = grossQty * ingredient.costPerPurchaseUnit / ingredient.purchaseConversionFactor
 */
@Entity
@Table(name = "recipe_ingredients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    /** Kitchen ingredient from the ingredient master. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private KitchenIngredient ingredient;

    /**
     * Net quantity required (in ingredient's base unit, e.g. 100 GRAM).
     * This is what goes into the dish after prep.
     */
    @Column(name = "net_qty", nullable = false, precision = 14, scale = 4)
    private BigDecimal netQty;

    /**
     * Prep waste / trim waste percentage (0-100).
     * e.g. 5 means 5% of the purchased amount is discarded during preparation.
     */
    @Column(name = "prep_waste_percent", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal prepWastePercent = BigDecimal.ZERO;

    /** Gross qty = netQty / (1 - prepWaste/100). Stored after calculation. */
    @Column(name = "gross_qty", precision = 14, scale = 4)
    private BigDecimal grossQty;

    /** Line cost in ₹. Stored after calculation. */
    @Column(name = "line_cost", precision = 14, scale = 4)
    private BigDecimal lineCost;
}
