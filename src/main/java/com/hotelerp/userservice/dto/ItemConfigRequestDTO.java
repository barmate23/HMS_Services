package com.hotelerp.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemConfigRequestDTO {
    private String itemCode;
    private String itemName;
    private Long categoryId;
    private Long uomId;
    private BigDecimal unitCost;
    private BigDecimal gstTaxRate;
    private String hsnSacCode;
    private BigDecimal reorderLevel;
    private BigDecimal onHandStock;
    private BigDecimal maxStockLevel;
    private BigDecimal minimumQty;
    private BigDecimal maximumQty;
    private String description;
    private Boolean isActive;
}
