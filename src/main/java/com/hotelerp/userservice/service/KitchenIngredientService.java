package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.KitchenIngredientPageResponse;
import com.hotelerp.userservice.dto.KitchenIngredientRequestDTO;
import com.hotelerp.userservice.dto.KitchenIngredientResponseDTO;

public interface KitchenIngredientService {

    StandardResponse<Void> createIngredient(KitchenIngredientRequestDTO dto);

    StandardResponse<KitchenIngredientResponseDTO> getIngredientById(Long id);

    /**
     * @param categoryId optional filter by category CommonMaster id
     * @param page       0-based page number
     * @param size       page size
     */
    StandardResponse<KitchenIngredientPageResponse> getAllIngredients(Long categoryId, int page, int size);

    StandardResponse<KitchenIngredientResponseDTO> updateIngredient(Long id, KitchenIngredientRequestDTO dto);

    StandardResponse<Void> deleteIngredient(Long id);
}
