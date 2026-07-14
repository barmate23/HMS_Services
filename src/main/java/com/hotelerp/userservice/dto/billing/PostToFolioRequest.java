package com.hotelerp.userservice.dto.billing;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostToFolioRequest {
    private Long roomId;        // Required: used to resolve the active folio
    private String source;      // e.g. Room, POS, Laundry
    private BigDecimal amount;
    private String taxType;     // e.g. GST 12%
    private String description;
}
