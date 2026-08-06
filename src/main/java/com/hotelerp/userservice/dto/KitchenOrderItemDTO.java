package com.hotelerp.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KitchenOrderItemDTO {
    private Long id;
    private String itemName;
    private Integer quantity;
    private Integer readyQuantity;
    /** Item-level KOT status code: KOT_SEND | IN_PROGRESS | KOT_READY */
    private String kotStatusCode;
    /** Human-readable label for display */
    private String kotStatusName;
}
