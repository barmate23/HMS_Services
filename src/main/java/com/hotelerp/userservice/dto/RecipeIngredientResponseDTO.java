package com.hotelerp.userservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecipeIngredientResponseDTO {

    private Long id;
    private Long hotelId;
    private String hotelName;

    // Ingredient reference
    private Long ingredientId;
    private String ingredientName;
    private String ingredientCode;

    // Unit info (resolved from ingredient.baseUnit)
    private String unitName;

    // Cost per base unit – resolved from ingredient
    private BigDecimal costPerBaseUnit;

    // User input fields
    private BigDecimal netQty;
    private BigDecimal prepWastePercent;

    // Calculated fields
    private BigDecimal grossQty;
    private BigDecimal lineCost;
}
