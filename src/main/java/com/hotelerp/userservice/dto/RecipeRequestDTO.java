package com.hotelerp.userservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecipeRequestDTO {

    private Long menuItemId;
    private String recipeName;
    private BigDecimal portionSize;
    private String portionUnit;
    private Integer prepTimeMins;
    private String cookingInstructions;

    private List<RecipeIngredientRequestDTO> ingredients;
}
