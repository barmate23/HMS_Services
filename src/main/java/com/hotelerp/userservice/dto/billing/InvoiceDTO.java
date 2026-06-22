package com.hotelerp.userservice.dto.billing;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceDTO {
    private Long id;
    private String invoiceNumber;
    private String folioNumber;
    private String roomNumber;
    private String guestName;
    private BigDecimal amount;
    private String status;
    private LocalDateTime date;
}
