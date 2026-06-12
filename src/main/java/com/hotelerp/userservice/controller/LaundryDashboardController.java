package com.hotelerp.userservice.controller;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.laundrydashboard.LaundryDashboardDTO;
import com.hotelerp.userservice.service.LaundryDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hmsService/v1/laundry/dashboard")
@RequiredArgsConstructor
public class LaundryDashboardController {

    private final LaundryDashboardService laundryDashboardService;

    @GetMapping("/getLaundryDashboardData")
    public ResponseEntity<StandardResponse<LaundryDashboardDTO>> getLaundryDashboardData() {
        StandardResponse<LaundryDashboardDTO> response = laundryDashboardService.getLaundryDashboardData();
        return ResponseEntity.ok(response);
    }
}
