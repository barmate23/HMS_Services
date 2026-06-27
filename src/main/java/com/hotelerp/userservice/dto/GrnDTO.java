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
public class GrnDTO {
    private Long id;
    private String grnNumber;

    private Long purchaseOrderId;
    private String poNumber;
    private String supplierName;

    private String receivedBy;
    private LocalDate receivedDate;
    private BigDecimal acceptedValue;
    private String varianceNote;
    
    private VendorBillDTO vendorBill;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
