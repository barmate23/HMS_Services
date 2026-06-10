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
public class PosItemStatDTO {
    private String itemName;
    private String category;
    private int soldQty;
    private BigDecimal rate;
    private BigDecimal avgRate;
    private List<MonthlyStatDTO> monthlyTrend;
    private BigDecimal totalValue;
    private String imageUrl;
}
