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
public class MinibarConsumptionDTO {
    private Long id;
    
    private Long roomId;
    private String roomNumber;
    
    private Long itemId;
    private String itemName;
    private String itemCode;
    
    private Integer parLevel;
    private Integer currentQty;
    private Integer consumedQty;
    private BigDecimal chargeAmount;
    
    private Long statusId;
    private String statusName;
    private String statusCode;
    
    private String remarks;
    private LocalDateTime consumptionDate;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
