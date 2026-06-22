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
public class SupplierDTO {
    private Long id;
    private String supplierName;

    private Long categoryId;
    private String categoryName;

    private String contactPerson;
    private String phone;
    private String email;

    private Long paymentTermsId;
    private String paymentTermsName;

    private String supplierAddress;
    private String city;
    private String state;
    private String pinCode;
    private String gstin;
    private String pan;
    private BigDecimal creditLimit;
    private String bankName;
    private String accountNumber;
    private String ifscCode;

    private Long statusId;
    private String statusName;
    private String statusCode;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
