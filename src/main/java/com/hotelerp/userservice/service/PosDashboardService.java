package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.posdashboard.PosDashboardCardsDTO;
import com.hotelerp.userservice.dto.posdashboard.PosOpsDashboardDTO;

import java.time.LocalDateTime;

public interface PosDashboardService {
    StandardResponse<PosOpsDashboardDTO> getPosDashboardData();
    StandardResponse<PosDashboardCardsDTO> getPosDashboardCards(Long outletId, LocalDateTime startDate, LocalDateTime endDate);
}
