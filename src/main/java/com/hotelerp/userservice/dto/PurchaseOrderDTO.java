package com.hotelerp.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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

    private LocalDate expectedDate;
    private Integer itemCount;
    private String poNote;
    private BigDecimal totalAmount;

    private Long statusId;
    private String statusName;
    private String statusCode;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
