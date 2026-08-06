package com.hotelerp.userservice.controller;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.constant.ServiceConstant;
import com.hotelerp.userservice.dto.RecipeDTO;
import com.hotelerp.userservice.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hmsService/v1/pos/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    /**
     * POST /api/hmsService/v1/pos/recipes/createRecipe
     *
     * Body:
     * {
     *   "menuItemId": 1,
     *   "recipeName": "Paneer Tikka Recipe",
     *   "portionSize": 1,
     *   "portionUnit": "PLATE",
     *   "prepTimeMins": 20,
     *   "cookingInstructions": "...",
     *   "ingredients": [
     *     { "ingredientId": 1, "netQty": 100, "prepWastePercent": 5 },
     *     { "ingredientId": 2, "netQty": 100, "prepWastePercent": 0 }
     *   ]
     * }
     */
    @PostMapping(ServiceConstant.CREATE_RECIPE)
    public ResponseEntity<StandardResponse<Void>> createRecipe(@RequestBody RecipeDTO dto) {
        StandardResponse<Void> response = recipeService.createRecipe(dto);
        HttpStatus status = response.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    /**
     * GET /api/hmsService/v1/pos/recipes/getRecipeById/{id}
     */
    @GetMapping(ServiceConstant.GET_RECIPE_BY_ID)
    public ResponseEntity<StandardResponse<RecipeDTO>> getRecipeById(@PathVariable Long id) {
        StandardResponse<RecipeDTO> response = recipeService.getRecipeById(id);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(response);
    }

    /**
     * GET /api/hmsService/v1/pos/recipes/getRecipeByMenuItemId/{menuItemId}
     * Useful when the UI wants to load the recipe for a selected dish.
     */
    @GetMapping(ServiceConstant.GET_RECIPE_BY_MENU_ITEM_ID)
    public ResponseEntity<StandardResponse<RecipeDTO>> getRecipeByMenuItemId(@PathVariable Long menuItemId) {
        StandardResponse<RecipeDTO> response = recipeService.getRecipeByMenuItemId(menuItemId);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(response);
    }

    /**
     * GET /api/hmsService/v1/pos/recipes/getAllRecipes?page=0&size=10
     */
    @GetMapping(ServiceConstant.GET_ALL_RECIPES)
    public ResponseEntity<StandardResponse<List<RecipeDTO>>> getAllRecipes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        StandardResponse<List<RecipeDTO>> response = recipeService.getAllRecipes(page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/hmsService/v1/pos/recipes/updateRecipe/{id}
     * Send only fields you want to change; BOM lines are updated in-place if id is provided.
     */
    @PutMapping(ServiceConstant.UPDATE_RECIPE)
    public ResponseEntity<StandardResponse<RecipeDTO>> updateRecipe(
            @PathVariable Long id,
            @RequestBody RecipeDTO dto) {
        StandardResponse<RecipeDTO> response = recipeService.updateRecipe(id, dto);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    /**
     * DELETE /api/hmsService/v1/pos/recipes/deleteRecipe/{id}
     * Soft-deletes the recipe (isDeleted = true).
     */
    @DeleteMapping(ServiceConstant.DELETE_RECIPE)
    public ResponseEntity<StandardResponse<Void>> deleteRecipe(@PathVariable Long id) {
        StandardResponse<Void> response = recipeService.deleteRecipe(id);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(response);
    }
}
