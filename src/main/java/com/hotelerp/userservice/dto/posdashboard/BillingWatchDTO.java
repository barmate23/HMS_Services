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
public class BillingWatchDTO {
    private int openBillsCount;
    private BigDecimal openBillsAmount;
    private int roomPostingPendingCount;
    private BigDecimal roomPostingPendingAmount;
    private int voidsCount;
    private BigDecimal voidsAmount;
}
