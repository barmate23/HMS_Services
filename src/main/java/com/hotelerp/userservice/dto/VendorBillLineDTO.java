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
public class VendorBillLineDTO {
    private Long id;
    private Long vendorBillId;
    private Long itemId;
    private String itemName;
    private BigDecimal receivedQuantity;
    private BigDecimal rate;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
}
