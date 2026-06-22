package com.hotelerp.userservice.controller;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.dashboard.InventoryDashboardDTO;
import com.hotelerp.userservice.service.InventoryDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventory/dashboard")
@RequiredArgsConstructor
public class InventoryDashboardController {

    private final InventoryDashboardService dashboardService;

    @GetMapping
    public ResponseEntity<StandardResponse<InventoryDashboardDTO>> getDashboard() {
        return ResponseEntity.ok(dashboardService.getDashboardData());
    }
}
