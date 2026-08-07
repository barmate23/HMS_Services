package com.hotelerp.userservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecipeIngredientRequestDTO {

    /** Optional BOM line ID (used when updating an existing line) */
    private Long id;

    /** Ingredient ID reference */
    private Long ingredientId;

    /** Net quantity in base unit (e.g. 100 GRAM) */
    private BigDecimal netQty;

    /** Prep / trim waste percentage 0-100 */
    private BigDecimal prepWastePercent;
}
