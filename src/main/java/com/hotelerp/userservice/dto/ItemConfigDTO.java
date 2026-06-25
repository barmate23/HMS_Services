package com.hotelerp.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemConfigDTO {
    private Long id;
    private String itemCode;
    private String itemName;
    
    private Long categoryId;
    private String categoryName;
    
    private Long uomId;
    private String uomName;
    
    private BigDecimal unitCost;
    private BigDecimal gstTaxRate;
    private String hsnSacCode;
    
    private BigDecimal reorderLevel;
    private BigDecimal maxStockLevel;
    
    private String description;
    private Boolean isActive;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
