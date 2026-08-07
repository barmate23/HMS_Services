package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.RecipeIngredientRequestDTO;
import com.hotelerp.userservice.dto.RecipeIngredientResponseDTO;
import com.hotelerp.userservice.dto.RecipePageResponse;
import com.hotelerp.userservice.dto.RecipeRequestDTO;
import com.hotelerp.userservice.dto.RecipeResponseDTO;
import com.hotelerp.userservice.entity.KitchenIngredient;
import com.hotelerp.userservice.entity.MenuItem;
import com.hotelerp.userservice.entity.Recipe;
import com.hotelerp.userservice.entity.RecipeIngredient;
import com.hotelerp.userservice.exception.ResourceNotFoundException;
import com.hotelerp.userservice.repository.KitchenIngredientRepository;
import com.hotelerp.userservice.repository.MenuItemRepository;
import com.hotelerp.userservice.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;
    private final MenuItemRepository menuItemRepository;
    private final KitchenIngredientRepository ingredientRepository;

    // ──────────────────────────────────────────────────────────────────────
    //  CREATE
    // ──────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public StandardResponse<Void> createRecipe(RecipeRequestDTO dto) {
        try {
            if (dto.getMenuItemId() == null) {
                return StandardResponse.error("menuItemId is required", "VALIDATION_ERROR", "menuItemId is mandatory");
            }

            MenuItem menuItem = menuItemRepository.findById(dto.getMenuItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with ID: " + dto.getMenuItemId()));

            if (recipeRepository.existsByMenuItemIdAndIsDeletedFalse(dto.getMenuItemId())) {
                return StandardResponse.error(
                        "A recipe already exists for menu item: " + menuItem.getItemName(),
                        "DUPLICATE_ERROR", "Only one active recipe is allowed per menu item");
            }

            Recipe recipe = Recipe.builder()
                    .menuItem(menuItem)
                    .recipeName(dto.getRecipeName() != null ? dto.getRecipeName().trim() : menuItem.getItemName() + " Recipe")
                    .portionSize(dto.getPortionSize())
                    .portionUnit(dto.getPortionUnit())
                    .prepTimeMins(dto.getPrepTimeMins())
                    .cookingInstructions(dto.getCookingInstructions())
                    .build();

            buildBomLines(recipe, dto.getIngredients());
            recipeRepository.save(recipe);
            return StandardResponse.success("Recipe created successfully");
        } catch (ResourceNotFoundException e) {
            return StandardResponse.error(e.getMessage(), "RESOURCE_NOT_FOUND", e.getMessage());
        } catch (Exception e) {
            log.error("Error creating recipe: ", e);
            return StandardResponse.error("Failed to create recipe", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    // ──────────────────────────────────────────────────────────────────────
    //  GET BY ID
    // ──────────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public StandardResponse<RecipeResponseDTO> getRecipeById(Long id) {
        try {
            Recipe recipe = recipeRepository.findByIdAndIsDeletedFalse(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with ID: " + id));
            return StandardResponse.success(toResponseDTO(recipe), "Recipe fetched successfully");
        } catch (ResourceNotFoundException e) {
            return StandardResponse.error(e.getMessage(), "NOT_FOUND", e.getMessage());
        } catch (Exception e) {
            log.error("Error fetching recipe: ", e);
            return StandardResponse.error("Failed to fetch recipe", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    // ──────────────────────────────────────────────────────────────────────
    //  GET BY MENU ITEM ID
    // ──────────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public StandardResponse<RecipeResponseDTO> getRecipeByMenuItemId(Long menuItemId) {
        try {
            Recipe recipe = recipeRepository.findByMenuItemIdAndIsDeletedFalse(menuItemId)
                    .orElseThrow(() -> new ResourceNotFoundException("No recipe found for menu item ID: " + menuItemId));
            return StandardResponse.success(toResponseDTO(recipe), "Recipe fetched successfully");
        } catch (ResourceNotFoundException e) {
            return StandardResponse.error(e.getMessage(), "NOT_FOUND", e.getMessage());
        } catch (Exception e) {
            log.error("Error fetching recipe by menu item: ", e);
            return StandardResponse.error("Failed to fetch recipe", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    // ──────────────────────────────────────────────────────────────────────
    //  GET ALL (paginated with summary metrics)
    // ──────────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public StandardResponse<RecipePageResponse> getAllRecipes(int page, int size) {
        try {
            Page<Recipe> pageResult = recipeRepository.findAllActive(PageRequest.of(page, size));
            List<RecipeResponseDTO> dtos = pageResult.getContent().stream()
                    .map(this::toResponseDTO)
                    .collect(Collectors.toList());

            // Compute summary overview metrics across ALL active recipes
            List<Recipe> allActive = recipeRepository.findAllActiveList();
            long totalRecipes = allActive.size();

            BigDecimal avgFoodCost = BigDecimal.ZERO;
            BigDecimal avgMargin = BigDecimal.ZERO;

            if (totalRecipes > 0) {
                BigDecimal sumFoodCostPct = BigDecimal.ZERO;
                BigDecimal sumMarginPct = BigDecimal.ZERO;

                for (Recipe r : allActive) {
                    RecipeResponseDTO dto = toResponseDTO(r);
                    if (dto.getFoodCostPercent() != null) {
                        sumFoodCostPct = sumFoodCostPct.add(dto.getFoodCostPercent());
                    }
                    if (dto.getGrossMarginPercent() != null) {
                        sumMarginPct = sumMarginPct.add(dto.getGrossMarginPercent());
                    }
                }

                avgFoodCost = sumFoodCostPct.divide(BigDecimal.valueOf(totalRecipes), 2, RoundingMode.HALF_UP);
                avgMargin = sumMarginPct.divide(BigDecimal.valueOf(totalRecipes), 2, RoundingMode.HALF_UP);
            }

            RecipePageResponse response = RecipePageResponse.builder()
                    .totalRecipes(totalRecipes)
                    .total(totalRecipes)
                    .avgFoodCostPercent(avgFoodCost)
                    .avgFoodCost(avgFoodCost)
                    .avgMarginPercent(avgMargin)
                    .avgMargin(avgMargin)
                    .recipes(dtos)
                    .totalRecords(pageResult.getTotalElements())
                    .currentPage(page)
                    .pageSize(size)
                    .totalPages(pageResult.getTotalPages())
                    .build();

            return StandardResponse.success(response, "Recipes fetched successfully");
        } catch (Exception e) {
            log.error("Error fetching recipes: ", e);
            return StandardResponse.error("Failed to fetch recipes", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    // ──────────────────────────────────────────────────────────────────────
    //  UPDATE
    // ──────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public StandardResponse<RecipeResponseDTO> updateRecipe(Long id, RecipeRequestDTO dto) {
        try {
            Recipe recipe = recipeRepository.findByIdAndIsDeletedFalse(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with ID: " + id));

            // If changing the linked menu item, check no other recipe exists for the target
            if (dto.getMenuItemId() != null && !dto.getMenuItemId().equals(recipe.getMenuItem().getId())) {
                if (recipeRepository.existsByMenuItemIdAndIsDeletedFalseAndIdNot(dto.getMenuItemId(), id)) {
                    return StandardResponse.error(
                            "A recipe already exists for the selected menu item",
                            "DUPLICATE_ERROR", "Only one active recipe allowed per menu item");
                }
                MenuItem menuItem = menuItemRepository.findById(dto.getMenuItemId())
                        .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with ID: " + dto.getMenuItemId()));
                recipe.setMenuItem(menuItem);
            }

            if (dto.getRecipeName() != null) recipe.setRecipeName(dto.getRecipeName().trim());
            if (dto.getPortionSize() != null) recipe.setPortionSize(dto.getPortionSize());
            if (dto.getPortionUnit() != null) recipe.setPortionUnit(dto.getPortionUnit());
            if (dto.getPrepTimeMins() != null) recipe.setPrepTimeMins(dto.getPrepTimeMins());
            if (dto.getCookingInstructions() != null) recipe.setCookingInstructions(dto.getCookingInstructions());

            if (dto.getIngredients() != null) {
                // ─ Map incoming lines by id for in-place updates ─
                Map<Long, RecipeIngredient> existingById = recipe.getIngredients().stream()
                        .filter(i -> i.getId() != null)
                        .collect(Collectors.toMap(RecipeIngredient::getId, i -> i, (a, b) -> a));

                List<RecipeIngredient> updatedLines = new ArrayList<>();
                for (RecipeIngredientRequestDTO lineDto : dto.getIngredients()) {
                    if (lineDto.getId() != null && existingById.containsKey(lineDto.getId())) {
                        // Update in-place
                        RecipeIngredient line = existingById.get(lineDto.getId());
                        if (lineDto.getIngredientId() != null) {
                            KitchenIngredient ing = ingredientRepository.findByIdAndIsDeletedFalse(lineDto.getIngredientId())
                                    .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found: " + lineDto.getIngredientId()));
                            line.setIngredient(ing);
                        }
                        if (lineDto.getNetQty() != null) line.setNetQty(lineDto.getNetQty());
                        if (lineDto.getPrepWastePercent() != null) line.setPrepWastePercent(lineDto.getPrepWastePercent());
                        computeGrossAndCost(line);
                        updatedLines.add(line);
                    } else {
                        // New line
                        RecipeIngredient line = buildLine(recipe, lineDto);
                        updatedLines.add(line);
                    }
                }

                recipe.getIngredients().clear();
                recipe.getIngredients().addAll(updatedLines);
            }

            // Recompute portion cost
            recipe.setPortionCost(sumPortionCost(recipe.getIngredients()));
            Recipe saved = recipeRepository.save(recipe);
            return StandardResponse.success(toResponseDTO(saved), "Recipe updated successfully");
        } catch (ResourceNotFoundException e) {
            return StandardResponse.error(e.getMessage(), "NOT_FOUND", e.getMessage());
        } catch (Exception e) {
            log.error("Error updating recipe: ", e);
            return StandardResponse.error("Failed to update recipe", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    // ──────────────────────────────────────────────────────────────────────
    //  DELETE (soft-delete)
    // ──────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public StandardResponse<Void> deleteRecipe(Long id) {
        try {
            Recipe recipe = recipeRepository.findByIdAndIsDeletedFalse(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with ID: " + id));
            recipe.setIsDeleted(true);
            recipeRepository.save(recipe);
            return StandardResponse.success("Recipe deleted successfully");
        } catch (ResourceNotFoundException e) {
            return StandardResponse.error(e.getMessage(), "NOT_FOUND", e.getMessage());
        } catch (Exception e) {
            log.error("Error deleting recipe: ", e);
            return StandardResponse.error("Failed to delete recipe", "INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

    // ──────────────────────────────────────────────────────────────────────
    //  PRIVATE HELPERS
    // ──────────────────────────────────────────────────────────────────────

    /** Build all BOM lines from DTOs and attach to recipe. Also sets portionCost. */
    private void buildBomLines(Recipe recipe, List<RecipeIngredientRequestDTO> lineDtos) {
        if (lineDtos == null || lineDtos.isEmpty()) {
            recipe.setPortionCost(BigDecimal.ZERO);
            return;
        }
        List<RecipeIngredient> lines = new ArrayList<>();
        for (RecipeIngredientRequestDTO lineDto : lineDtos) {
            lines.add(buildLine(recipe, lineDto));
        }
        recipe.getIngredients().addAll(lines);
        recipe.setPortionCost(sumPortionCost(lines));
    }

    /** Build a single BOM line entity and compute grossQty + lineCost. */
    private RecipeIngredient buildLine(Recipe recipe, RecipeIngredientRequestDTO lineDto) {
        if (lineDto.getIngredientId() == null) {
            throw new IllegalArgumentException("ingredientId is required for each BOM line");
        }
        KitchenIngredient ingredient = ingredientRepository.findByIdAndIsDeletedFalse(lineDto.getIngredientId())
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found with ID: " + lineDto.getIngredientId()));

        BigDecimal netQty = lineDto.getNetQty() != null ? lineDto.getNetQty() : BigDecimal.ZERO;
        BigDecimal prepWaste = lineDto.getPrepWastePercent() != null ? lineDto.getPrepWastePercent() : BigDecimal.ZERO;

        RecipeIngredient line = RecipeIngredient.builder()
                .recipe(recipe)
                .ingredient(ingredient)
                .netQty(netQty)
                .prepWastePercent(prepWaste)
                .build();

        computeGrossAndCost(line);
        return line;
    }

    /**
     * grossQty = netQty / (1 - prepWastePercent / 100)
     * lineCost = grossQty * (costPerPurchaseUnit / purchaseConversionFactor)
     *          = grossQty * effectiveCostPerBaseUnit
     */
    private void computeGrossAndCost(RecipeIngredient line) {
        BigDecimal netQty = line.getNetQty() != null ? line.getNetQty() : BigDecimal.ZERO;
        BigDecimal prepWaste = line.getPrepWastePercent() != null ? line.getPrepWastePercent() : BigDecimal.ZERO;

        // grossQty
        BigDecimal wasteFactor = BigDecimal.ONE.subtract(
                prepWaste.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP));
        BigDecimal grossQty;
        if (wasteFactor.compareTo(BigDecimal.ZERO) <= 0) {
            grossQty = netQty; // guard against 100% waste
        } else {
            grossQty = netQty.divide(wasteFactor, 4, RoundingMode.HALF_UP);
        }
        line.setGrossQty(grossQty);

        // lineCost = grossQty * costPerBaseUnit
        // costPerBaseUnit = costPerPurchaseUnit / purchaseConversionFactor
        KitchenIngredient ing = line.getIngredient();
        BigDecimal lineCost = BigDecimal.ZERO;
        if (ing != null && ing.getCostPerPurchaseUnit() != null) {
            BigDecimal convFactor = ing.getPurchaseConversionFactor() != null
                    && ing.getPurchaseConversionFactor().compareTo(BigDecimal.ZERO) > 0
                    ? ing.getPurchaseConversionFactor()
                    : BigDecimal.ONE;
            BigDecimal costPerBaseUnit = ing.getCostPerPurchaseUnit().divide(convFactor, 10, RoundingMode.HALF_UP);
            lineCost = grossQty.multiply(costPerBaseUnit).setScale(4, RoundingMode.HALF_UP);
        }
        line.setLineCost(lineCost);
    }

    /** Sum all line costs. */
    private BigDecimal sumPortionCost(List<RecipeIngredient> lines) {
        if (lines == null) return BigDecimal.ZERO;
        return lines.stream()
                .map(l -> l.getLineCost() != null ? l.getLineCost() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /** Convert Recipe entity → RecipeResponseDTO with all calculated fields. */
    private RecipeResponseDTO toResponseDTO(Recipe r) {
        MenuItem mi = r.getMenuItem();
        BigDecimal sellingPrice = mi != null && mi.getPrice() != null ? mi.getPrice() : BigDecimal.ZERO;
        BigDecimal portionCost = r.getPortionCost() != null ? r.getPortionCost() : BigDecimal.ZERO;

        BigDecimal foodCostPct = BigDecimal.ZERO;
        BigDecimal grossMarginPct = BigDecimal.valueOf(100);
        if (sellingPrice.compareTo(BigDecimal.ZERO) > 0) {
            foodCostPct = portionCost
                    .multiply(BigDecimal.valueOf(100))
                    .divide(sellingPrice, 2, RoundingMode.HALF_UP);
            grossMarginPct = BigDecimal.valueOf(100).subtract(foodCostPct).setScale(2, RoundingMode.HALF_UP);
        }

        String displayName = "";
        if (mi != null) {
            displayName = mi.getItemName();
            if (sellingPrice.compareTo(BigDecimal.ZERO) > 0) {
                displayName += " (₹" + sellingPrice.stripTrailingZeros().toPlainString() + ")";
            }
            if (mi.getCategory() != null && mi.getCategory().getValue() != null) {
                displayName += " • " + mi.getCategory().getValue();
            }
        }

        List<RecipeIngredientResponseDTO> ingredientDtos = r.getIngredients() == null ? List.of() :
                r.getIngredients().stream().map(this::toLineResponseDTO).collect(Collectors.toList());

        return RecipeResponseDTO.builder()
                .id(r.getId())
                .menuItemId(mi != null ? mi.getId() : null)
                .menuItemDisplayName(displayName)
                .recipeName(r.getRecipeName())
                .portionSize(r.getPortionSize())
                .portionUnit(r.getPortionUnit())
                .prepTimeMins(r.getPrepTimeMins())
                .cookingInstructions(r.getCookingInstructions())
                .ingredients(ingredientDtos)
                .portionCost(portionCost)
                .sellingPrice(sellingPrice)
                .foodCostPercent(foodCostPct)
                .grossMarginPercent(grossMarginPct)
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }

    /** Convert a single BOM line entity → Response DTO. */
    private RecipeIngredientResponseDTO toLineResponseDTO(RecipeIngredient line) {
        KitchenIngredient ing = line.getIngredient();
        BigDecimal costPerBaseUnit = BigDecimal.ZERO;
        if (ing != null && ing.getCostPerPurchaseUnit() != null) {
            BigDecimal convFactor = ing.getPurchaseConversionFactor() != null
                    && ing.getPurchaseConversionFactor().compareTo(BigDecimal.ZERO) > 0
                    ? ing.getPurchaseConversionFactor()
                    : BigDecimal.ONE;
            costPerBaseUnit = ing.getCostPerPurchaseUnit().divide(convFactor, 4, RoundingMode.HALF_UP);
        }

        return RecipeIngredientResponseDTO.builder()
                .id(line.getId())
                .ingredientId(ing != null ? ing.getId() : null)
                .ingredientName(ing != null ? ing.getIngredientName() : null)
                .ingredientCode(ing != null ? ing.getIngredientCode() : null)
                .unitName(ing != null && ing.getBaseUnit() != null ? ing.getBaseUnit().getValue() : null)
                .costPerBaseUnit(costPerBaseUnit)
                .netQty(line.getNetQty())
                .prepWastePercent(line.getPrepWastePercent())
                .grossQty(line.getGrossQty())
                .lineCost(line.getLineCost())
                .build();
    }
}
