package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.RecipeDTO;

import java.util.List;

public interface RecipeService {

    StandardResponse<Void> createRecipe(RecipeDTO dto);

    StandardResponse<RecipeDTO> getRecipeById(Long id);

    /** Get the recipe for a specific menu item. */
    StandardResponse<RecipeDTO> getRecipeByMenuItemId(Long menuItemId);

    /**
     * Paginated list of all recipes.
     * @param page 0-based page index
     * @param size records per page
     */
    StandardResponse<List<RecipeDTO>> getAllRecipes(int page, int size);

    StandardResponse<RecipeDTO> updateRecipe(Long id, RecipeDTO dto);

    StandardResponse<Void> deleteRecipe(Long id);
}
