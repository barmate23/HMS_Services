package com.hotelerp.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LaundryPriceMasterDTO {
    private Long id;
    private String category;
    private String itemName;
    private Double washFoldPrice;
    private Double washPressPrice;
    private Double dryCleanPrice;
    private Double expressSurchargePercentage;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
