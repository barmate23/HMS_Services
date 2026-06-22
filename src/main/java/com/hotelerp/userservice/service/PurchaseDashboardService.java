package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.dashboard.PurchaseDashboardDTO;

public interface PurchaseDashboardService {
    StandardResponse<PurchaseDashboardDTO> getDashboardData();
}
