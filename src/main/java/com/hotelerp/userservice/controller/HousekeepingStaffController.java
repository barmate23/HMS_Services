package com.hotelerp.userservice.controller;

import com.hotelerp.userservice.dto.StaffDTO;
import com.hotelerp.userservice.service.StaffService;
import com.hotelerp.userservice.constant.ServiceConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/housekeeping/staff")
@RequiredArgsConstructor
public class HousekeepingStaffController {

    private final StaffService staffService;

    @GetMapping(ServiceConstant.GET_HOUSEKEEPING_STAFF)
    public ResponseEntity<List<StaffDTO>> getHousekeepingStaff() {
        return ResponseEntity.ok(staffService.getHousekeepingStaff());
    }
}
