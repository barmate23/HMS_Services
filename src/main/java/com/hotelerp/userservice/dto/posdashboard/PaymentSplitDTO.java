package com.hotelerp.userservice.dto.posdashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSplitDTO {
    private String method;
    private double percentage;
    private BigDecimal amount;
}
