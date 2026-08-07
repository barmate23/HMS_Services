package com.hotelerp.userservice.controller;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.constant.ServiceConstant;
import com.hotelerp.userservice.dto.KitchenIngredientPageResponse;
import com.hotelerp.userservice.dto.KitchenIngredientRequestDTO;
import com.hotelerp.userservice.dto.KitchenIngredientResponseDTO;
import com.hotelerp.userservice.service.KitchenIngredientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hmsService/v1/pos/ingredients")
@RequiredArgsConstructor
public class KitchenIngredientController {

    private final KitchenIngredientService ingredientService;

    /**
     * POST /api/hmsService/v1/pos/ingredients/createIngredient
     * Body: { ingredientName, categoryId, baseUnitId, purchaseUnitId, ... }
     */
    @PostMapping(ServiceConstant.CREATE_INGREDIENT)
    public ResponseEntity<StandardResponse<Void>> createIngredient(@RequestBody KitchenIngredientRequestDTO dto) {
        StandardResponse<Void> response = ingredientService.createIngredient(dto);
        HttpStatus status = response.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    /**
     * GET /api/hmsService/v1/pos/ingredients/getIngredientById/{id}
     */
    @GetMapping(ServiceConstant.GET_INGREDIENT_BY_ID)
    public ResponseEntity<StandardResponse<KitchenIngredientResponseDTO>> getIngredientById(@PathVariable Long id) {
        StandardResponse<KitchenIngredientResponseDTO> response = ingredientService.getIngredientById(id);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(response);
    }

    /**
     * GET /api/hmsService/v1/pos/ingredients/getAllIngredients
     *   ?categoryId=  (optional, filter by CommonMaster id)
     *   &page=0       (0-based, default 0)
     *   &size=10      (default 10)
     *
     * Response includes top-level summary: total, lowStock, categories
     */
    @GetMapping(ServiceConstant.GET_ALL_INGREDIENTS)
    public ResponseEntity<StandardResponse<KitchenIngredientPageResponse>> getAllIngredients(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        StandardResponse<KitchenIngredientPageResponse> response = ingredientService.getAllIngredients(categoryId, page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/hmsService/v1/pos/ingredients/updateIngredient/{id}
     */
    @PutMapping(ServiceConstant.UPDATE_INGREDIENT)
    public ResponseEntity<StandardResponse<KitchenIngredientResponseDTO>> updateIngredient(
            @PathVariable Long id,
            @RequestBody KitchenIngredientRequestDTO dto) {
        StandardResponse<KitchenIngredientResponseDTO> response = ingredientService.updateIngredient(id, dto);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    /**
     * DELETE /api/hmsService/v1/pos/ingredients/deleteIngredient/{id}
     * Soft-deletes the ingredient (sets isDeleted = true).
     */
    @DeleteMapping(ServiceConstant.DELETE_INGREDIENT)
    public ResponseEntity<StandardResponse<Void>> deleteIngredient(@PathVariable Long id) {
        StandardResponse<Void> response = ingredientService.deleteIngredient(id);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(response);
    }
}
