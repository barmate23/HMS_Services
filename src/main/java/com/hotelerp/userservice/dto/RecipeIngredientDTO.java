package com.hotelerp.userservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * @deprecated Use {@link RecipeIngredientRequestDTO} for requests and {@link RecipeIngredientResponseDTO} for responses.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Deprecated
public class RecipeIngredientDTO extends RecipeIngredientResponseDTO {
}

