package com.hotelerp.userservice.controller;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.dashboard.PurchaseDashboardDTO;
import com.hotelerp.userservice.service.PurchaseDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hmsService/v1/purchase/dashboard")
@RequiredArgsConstructor
public class PurchaseDashboardController {

    private final PurchaseDashboardService dashboardService;

    @GetMapping("/getPurchaseDashboard")
    public ResponseEntity<StandardResponse<PurchaseDashboardDTO>> getDashboard() {
        return ResponseEntity.ok(dashboardService.getDashboardData());
    }
}
