package com.hotelerp.userservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Response wrapper for the paginated GET /getAllRecipes API.
 * Includes top-level overview metrics: totalRecipes, avgFoodCostPercent, avgMarginPercent.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecipePageResponse {

    // ── Overview metrics ──────────────────────────────────────────────────
    private long totalRecipes;
    private long total;

    private BigDecimal avgFoodCostPercent;
    private BigDecimal avgFoodCost;

    private BigDecimal avgMarginPercent;
    private BigDecimal avgMargin;

    // ── Paginated list ────────────────────────────────────────────────────
    private List<RecipeResponseDTO> recipes;

    private long totalRecords;
    private int currentPage;
    private int pageSize;
    private int totalPages;
}
