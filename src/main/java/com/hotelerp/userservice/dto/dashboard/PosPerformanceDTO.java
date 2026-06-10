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
public class PosPerformanceDTO {
    private BigDecimal orderValue;
    private BigDecimal avgOrder;
    private int menuItemsCount;
    private List<PosItemStatDTO> topSellingItems;
    private List<PosItemStatDTO> lessSellingItems;
}
