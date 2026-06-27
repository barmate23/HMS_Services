package com.hotelerp.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorBillDTO {
    private Long id;
    private String billNumber;

    private Long supplierId;
    private String supplierName;

    private Long purchaseOrderId;
    private String poNumber;

    private LocalDate billDate;
    private LocalDate dueDate;
    private BigDecimal amountBeforeTax;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;

    private Long statusId;
    private String statusName;
    private String statusCode;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Builder.Default
    private List<VendorBillLineDTO> lines = new ArrayList<>();
}
