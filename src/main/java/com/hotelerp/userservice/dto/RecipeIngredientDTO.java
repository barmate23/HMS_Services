package com.hotelerp.userservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.math.BigDecimal;

/**
 * One BOM line inside RecipeDTO.
 * Mirrors the UI table columns:
 *   INGREDIENT | NET QTY | UNIT | PREP WASTE % | GROSS QTY | COST (₹)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecipeIngredientDTO {

    private Long id;

    // Ingredient reference
    private Long ingredientId;
    private String ingredientName;
    private String ingredientCode;

    // Unit info (resolved from ingredient.baseUnit)
    private String unitName;

    // Cost per base unit – resolved from ingredient, sent back for UI display
    private BigDecimal costPerBaseUnit;

    // Fields entered by the user
    /** Net quantity in base unit (e.g. 100 GRAM) */
    private BigDecimal netQty;

    /** Prep / trim waste percentage 0-100 */
    private BigDecimal prepWastePercent;

    // Calculated and returned in response
    /** grossQty = netQty / (1 - prepWastePercent/100) */
    private BigDecimal grossQty;

    /** lineCost = grossQty * costPerBaseUnit */
    private BigDecimal lineCost;
}
