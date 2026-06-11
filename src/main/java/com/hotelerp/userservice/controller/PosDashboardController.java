package com.hotelerp.userservice.controller;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.posdashboard.PosOpsDashboardDTO;
import com.hotelerp.userservice.service.PosDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hmsService/v1/pos/dashboard")
@RequiredArgsConstructor
public class PosDashboardController {

    private final PosDashboardService posDashboardService;

    @GetMapping("/getPosDashboardData")
    public ResponseEntity<StandardResponse<PosOpsDashboardDTO>> getPosDashboardData() {
        StandardResponse<PosOpsDashboardDTO> response = posDashboardService.getPosDashboardData();
        return ResponseEntity.ok(response);
    }
}
