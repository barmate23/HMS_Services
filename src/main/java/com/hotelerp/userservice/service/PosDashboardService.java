package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.posdashboard.PosOpsDashboardDTO;

public interface PosDashboardService {
    StandardResponse<PosOpsDashboardDTO> getPosDashboardData();
}
