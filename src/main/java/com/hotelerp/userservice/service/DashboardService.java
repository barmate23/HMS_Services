package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.dashboard.DashboardDTO;

public interface DashboardService {
    StandardResponse<DashboardDTO> getDashboardData(String financialYear);
}
