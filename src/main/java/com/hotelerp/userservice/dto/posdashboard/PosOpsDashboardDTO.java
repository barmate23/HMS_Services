package com.hotelerp.userservice.dto.posdashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PosOpsDashboardDTO {
    private PosDashboardCardsDTO cards;
    private FloorPulseDTO floorPulse;
    private List<KotQueueDTO> kotQueue;
    private List<OutletRevenueDTO> revenueMix;
    private List<PaymentSplitDTO> paymentSplit;
    private List<FastMovingItemDTO> fastMovingItems;
    private List<FastMovingItemDTO> lessMovingItems;
    private BillingWatchDTO billingWatch;
    private List<RecentActivityDTO> recentActivity;
    private java.math.BigDecimal orderValue;
    private java.math.BigDecimal avgOrder;
    private Integer menuItemsCount;
}
