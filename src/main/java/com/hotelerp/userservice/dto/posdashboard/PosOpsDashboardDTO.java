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
    private FloorPulseDTO floorPulse;
    private List<KotQueueDTO> kotQueue;
    private List<OutletRevenueDTO> revenueMix;
    private List<PaymentSplitDTO> paymentSplit;
    private List<FastMovingItemDTO> fastMovingItems;
    private BillingWatchDTO billingWatch;
    private List<RecentActivityDTO> recentActivity;
}
