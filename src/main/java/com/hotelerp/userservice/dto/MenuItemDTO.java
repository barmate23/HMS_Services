package com.hotelerp.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItemDTO {
    private Long id;
    private Long outletId;
    private String outletName;
    private String itemName;
    private String category;
    private String subcategory;
    private String imageUrl;
    private BigDecimal price;
    private BigDecimal taxPercent;
    private String variants;
    private String modifiers;
    private BigDecimal happyHourPrice;
    private String happyHourWindow;
    private String linkedStockItem;
    private Boolean isAvailable;
    private Boolean isFeatured;
}
