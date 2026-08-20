package com.hotelerp.userservice.dto.posdashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FastMovingItemDTO {
    private String itemName;
    private String outletName;
    private int soldQty;
    private String imageUrl;
    private byte[] itemImage;
    private String categoryName;
    private String itemType;
    private java.math.BigDecimal rate;
    private java.math.BigDecimal totalAmount;
    private java.util.Map<String, Integer> monthlySales;
}
