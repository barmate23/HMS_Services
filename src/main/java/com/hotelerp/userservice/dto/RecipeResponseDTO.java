package com.hotelerp.userservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecipeResponseDTO {

    private Long id;

    // Menu item reference
    private Long menuItemId;
    private String menuItemDisplayName;

    // Recipe header
    private String recipeName;
    private BigDecimal portionSize;
    private String portionUnit;
    private Integer prepTimeMins;
    private String cookingInstructions;

    // BOM lines
    private List<RecipeIngredientResponseDTO> ingredients;

    // Calculated fields
    private BigDecimal portionCost;
    private BigDecimal sellingPrice;
    private BigDecimal foodCostPercent;
    private BigDecimal grossMarginPercent;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
