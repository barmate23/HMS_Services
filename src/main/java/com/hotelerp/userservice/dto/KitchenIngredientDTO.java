package com.hotelerp.userservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * @deprecated Use {@link KitchenIngredientRequestDTO} for requests and {@link KitchenIngredientResponseDTO} for responses.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Deprecated
public class KitchenIngredientDTO extends KitchenIngredientResponseDTO {
}

