package com.hotelerp.userservice.controller;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.constant.ServiceConstant;
import com.hotelerp.userservice.dto.posdashboard.PosDashboardCardsDTO;
import com.hotelerp.userservice.dto.posdashboard.PosOpsDashboardDTO;
import com.hotelerp.userservice.service.PosDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

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

    @GetMapping(ServiceConstant.GET_POS_DASHBOARD_CARDS)
    public ResponseEntity<StandardResponse<PosDashboardCardsDTO>> getPosDashboardCards(
            @RequestParam(required = false) Long outletId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        StandardResponse<PosDashboardCardsDTO> response = posDashboardService.getPosDashboardCards(outletId, startDate,
                endDate);
        return ResponseEntity.ok(response);
    }
}
