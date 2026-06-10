package com.hotelerp.userservice.controller;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.dashboard.DashboardDTO;
import com.hotelerp.userservice.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hmsService/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/getDashboardData")
    public ResponseEntity<StandardResponse<DashboardDTO>> getDashboardData(
            @RequestParam(defaultValue = "FY 2026-27") String financialYear) {
        StandardResponse<DashboardDTO> response = dashboardService.getDashboardData(financialYear);
        return ResponseEntity.ok(response);
    }
}
