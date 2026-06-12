package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.laundrydashboard.LaundryDashboardDTO;

public interface LaundryDashboardService {
    StandardResponse<LaundryDashboardDTO> getLaundryDashboardData();
}
