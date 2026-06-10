package com.hotelerp.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LaundryOrderItemDTO {
    private Long id;
    private Long priceMasterId;
    private String itemName;
    private Integer quantity;
    private Double unitPrice;
    private Double total;
    private String notes;
}
