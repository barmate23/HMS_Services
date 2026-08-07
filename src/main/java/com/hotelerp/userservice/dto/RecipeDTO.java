package com.hotelerp.userservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * @deprecated Use {@link RecipeRequestDTO} for requests and {@link RecipeResponseDTO} for responses.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Deprecated
public class RecipeDTO extends RecipeResponseDTO {
}

