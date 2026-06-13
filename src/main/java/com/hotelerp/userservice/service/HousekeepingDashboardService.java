package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.hkdashboard.HousekeepingDashboardDTO;

import com.hotelerp.userservice.dto.hkdashboard.UpdateHkStatusRequest;

public interface HousekeepingDashboardService {
    StandardResponse<HousekeepingDashboardDTO> getHousekeepingDashboardData();
    StandardResponse<Void> updateRoomHkStatus(UpdateHkStatusRequest request);
}