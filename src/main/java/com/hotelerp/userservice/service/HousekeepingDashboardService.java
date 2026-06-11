package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.hkdashboard.HousekeepingDashboardDTO;

public interface HousekeepingDashboardService {
    StandardResponse<HousekeepingDashboardDTO> getHousekeepingDashboardData();
}