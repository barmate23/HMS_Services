package com.hotelerp.userservice.dto.billing;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FolioPostingRequest {
    private Long folioId;
    private String source; // Room, POS, Laundry
    private BigDecimal amount;
    private String taxType; // e.g. GST 12%
    private String description;
    private BigDecimal paidAmount;
}
