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
public class PosDashboardCardsDTO {
    private Integer activeOutlets;
    private Integer openOrders;
    private Integer kotRunning;
    private Integer bills;
    private Integer roomPostings;
    private BigDecimal grossSales;
}
