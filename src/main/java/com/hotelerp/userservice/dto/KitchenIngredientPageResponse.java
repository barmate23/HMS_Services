package com.hotelerp.userservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.util.List;

/**
 * Response wrapper for the paginated GET /getAllIngredients API.
 * Includes a top-level summary (total, low stock, categories) plus the paginated data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KitchenIngredientPageResponse {

    // ── Summary cards shown at the top of the list page ──────────────────
    private long total;
    private long lowStock;
    private long categories;

    // ── Paginated list ────────────────────────────────────────────────────
    private List<KitchenIngredientResponseDTO> ingredients;
    private long totalRecords;
    private int currentPage;
    private int pageSize;
    private int totalPages;
}
