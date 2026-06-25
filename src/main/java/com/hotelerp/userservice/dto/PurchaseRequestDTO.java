package com.hotelerp.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseRequestDTO {
    private Long id;
    private String prNumber;

    private Long departmentId;
    private String departmentName;

    private String requestedBy;
    private LocalDate neededBy;
    private BigDecimal expectedAmount;
    private String justification;

    private Long statusId;
    private String statusName;
    private String statusCode;

    private List<PurchaseRequestItemDTO> items;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PurchaseRequestItemDTO {
        private Long id;
        private Long itemId;
        private String itemName;
        private String itemCode;
        private java.math.BigDecimal requiredQuantity;
        private BigDecimal unitPrice;
    }
}
