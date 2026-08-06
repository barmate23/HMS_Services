package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.KitchenIngredientDTO;
import com.hotelerp.userservice.dto.KitchenIngredientPageResponse;

public interface KitchenIngredientService {

    StandardResponse<Void> createIngredient(KitchenIngredientDTO dto);

    StandardResponse<KitchenIngredientDTO> getIngredientById(Long id);

    /**
     * @param categoryId optional filter by category CommonMaster id
     * @param page       0-based page number
     * @param size       page size
     */
    StandardResponse<KitchenIngredientPageResponse> getAllIngredients(Long categoryId, int page, int size);

    StandardResponse<KitchenIngredientDTO> updateIngredient(Long id, KitchenIngredientDTO dto);

    StandardResponse<Void> deleteIngredient(Long id);
}
