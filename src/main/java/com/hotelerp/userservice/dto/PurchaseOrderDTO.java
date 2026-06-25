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
public class PurchaseOrderDTO {
    private Long id;
    private String poNumber;

    private Long supplierId;
    private String supplierName;

    private Long departmentId;
    private String departmentName;

    private LocalDate poDate;
    private LocalDate expectedDate;
    
    private Long prId;
    private String prNumber;

    private Long deliveryStoreId;
    private String deliveryStoreName;

    private Long paymentTermsId;
    private String paymentTermsName;

    private String requestedBy;
    
    private Integer itemCount;
    private String poNote;
    private BigDecimal totalAmount;

    private List<PurchaseOrderLineDTO> lines;

    private Long statusId;
    private String statusName;
    private String statusCode;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PurchaseOrderLineDTO {
        private Long id;
        private Long itemId;
        private String itemCode;
        private String itemName;
        private BigDecimal quantity;
        private BigDecimal rate;
        private BigDecimal discountPercentage;
        private BigDecimal gstPercentage;
        private BigDecimal totalAmount;
    }

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
