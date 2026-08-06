package com.hotelerp.userservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Full recipe DTO – used for both create/update request body and API response.
 *
 * Computed fields (portionCost, foodCostPercent, grossMarginPercent, sellingPrice)
 * are populated in the response by the service layer and are read-only from the
 * client's perspective.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecipeDTO {

    private Long id;

    // ── Menu item reference ───────────────────────────────────────────────
    private Long menuItemId;
    /** e.g. "Paneer Tikka (₹290) • Main Course" */
    private String menuItemDisplayName;

    // ── Recipe header ─────────────────────────────────────────────────────
    private String recipeName;
    private BigDecimal portionSize;
    private String portionUnit;
    private Integer prepTimeMins;
    private String cookingInstructions;

    // ── BOM lines ─────────────────────────────────────────────────────────
    private List<RecipeIngredientDTO> ingredients;

    // ── Calculated (response only) ────────────────────────────────────────
    /** Sum of all lineCost values */
    private BigDecimal portionCost;

    /** MenuItem.price – returned for display on the form */
    private BigDecimal sellingPrice;

    /** (portionCost / sellingPrice) * 100 */
    private BigDecimal foodCostPercent;

    /** 100 - foodCostPercent */
    private BigDecimal grossMarginPercent;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
