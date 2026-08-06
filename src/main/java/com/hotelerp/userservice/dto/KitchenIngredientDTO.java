package com.hotelerp.userservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KitchenIngredientDTO {

    private Long id;
    private String ingredientCode;
    private String ingredientName;

    // Category
    private Long categoryId;
    private String categoryName;

    // Base Unit (recipe usage) - from CommonMaster
    private Long baseUnitId;
    private String baseUnitName;

    // Purchase Unit (procurement) - from CommonMaster
    private Long purchaseUnitId;
    private String purchaseUnitName;

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

    // Storage type - from CommonMaster
    private Long storageTypeId;
    private String storageTypeName;

    private String preferredSupplier;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
