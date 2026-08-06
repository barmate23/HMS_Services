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
public class PosOrderItemDTO {
    private Long id;
    private Long menuItemId;
    private String itemName;
    private Integer quantity;
    private Integer readyQuantity;
    private BigDecimal price;
    private BigDecimal subtotal;
    // Item-level KOT status (CommonMaster id / code / display value)
    private Long kotStatusId;
    private String kotStatusCode;
    private String kotStatusName;
}
