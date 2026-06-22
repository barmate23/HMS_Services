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
public class InventoryStockDTO {
    private Long id;
    private String itemCode;
    private String itemName;
    
    private Long categoryId;
    private String categoryName;
    
    private Long storeId;
    private String storeName;
    
    private BigDecimal onHand;
    private String unit;
    private BigDecimal reorderLevel;
    private BigDecimal parLevel;
    private BigDecimal unitCost;
    private BigDecimal totalValue;
    
    private Long statusId;
    private String statusName;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
