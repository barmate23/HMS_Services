package com.hotelerp.userservice.dto.billing;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FolioPaymentRequest {
    private Long folioId;
    private String mode; // UPI, Cash, Card
    private BigDecimal amount;
    private String referenceNumber;
    private String notes;
}
