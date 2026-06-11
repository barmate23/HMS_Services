package com.hotelerp.userservice.controller;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.hkdashboard.HousekeepingDashboardDTO;
import com.hotelerp.userservice.service.HousekeepingDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hmsService/v1/housekeeping/dashboard")
@RequiredArgsConstructor
public class HousekeepingDashboardController {

    private final HousekeepingDashboardService housekeepingDashboardService;

    @GetMapping("/getHkDashboardData")
    public ResponseEntity<StandardResponse<HousekeepingDashboardDTO>> getHkDashboardData() {
        StandardResponse<HousekeepingDashboardDTO> response = housekeepingDashboardService.getHousekeepingDashboardData();
        return ResponseEntity.ok(response);
    }
}
