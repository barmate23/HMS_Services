package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.dashboard.InventoryDashboardDTO;

public interface InventoryDashboardService {
    StandardResponse<InventoryDashboardDTO> getDashboardData();
}
