package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.RecipePageResponse;
import com.hotelerp.userservice.dto.RecipeRequestDTO;
import com.hotelerp.userservice.dto.RecipeResponseDTO;

public interface RecipeService {

    StandardResponse<Void> createRecipe(RecipeRequestDTO dto);

    StandardResponse<RecipeResponseDTO> getRecipeById(Long id);

    /** Get the recipe for a specific menu item. */
    StandardResponse<RecipeResponseDTO> getRecipeByMenuItemId(Long menuItemId);

    /**
     * Paginated list of all recipes with summary metrics.
     * @param page 0-based page index
     * @param size records per page
     */
    StandardResponse<RecipePageResponse> getAllRecipes(int page, int size);

    StandardResponse<RecipeResponseDTO> updateRecipe(Long id, RecipeRequestDTO dto);

    StandardResponse<Void> deleteRecipe(Long id);
}
