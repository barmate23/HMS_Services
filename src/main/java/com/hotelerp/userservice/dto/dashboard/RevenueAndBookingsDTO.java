package com.hotelerp.userservice.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueAndBookingsDTO {
    private List<MonthlyStatDTO> monthlyPerformance;
    private BigDecimal totalRevenue;
    private int totalBookings;
    private BigDecimal abv;
}
