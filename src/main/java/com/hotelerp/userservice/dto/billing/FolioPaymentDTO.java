package com.hotelerp.userservice.dto.billing;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FolioPaymentDTO {
    private Long id;
    private Long folioId;
    private String folioNumber;
    private String guestName;
    private String roomNumber;
    private String mode;
    private String referenceNumber;
    private String notes;
    private BigDecimal amount;
    private LocalDateTime paymentDate;
}
