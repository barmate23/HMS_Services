package com.hotelerp.userservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KitchenIngredientRequestDTO {

    private String ingredientName;

    // Category ID from CommonMaster
    private Long categoryId;

    // Base Unit ID (recipe usage) - from CommonMaster
    private Long baseUnitId;

    // Purchase Unit ID (procurement) - from CommonMaster
    private Long purchaseUnitId;

    // Conversion: 1 purchaseUnit = purchaseConversionFactor baseUnits
    private BigDecimal purchaseConversionFactor;

    // Usable yield % after prep/trimming
    private BigDecimal usableYieldPercent;

    // Cost per purchase unit
    private BigDecimal costPerPurchaseUnit;

    // Current stock level (in base unit)
    private BigDecimal currentStockLevel;

    // Reorder threshold level (in base unit)
    private BigDecimal reorderThresholdLevel;

    // Reorder quantity (in base unit)
    private BigDecimal reorderQuantity;

    // Storage type ID from CommonMaster
    private Long storageTypeId;

    private String preferredSupplier;
}
