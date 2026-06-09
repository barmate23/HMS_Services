package com.hotelerp.userservice.controller;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.StaffDTO;
import com.hotelerp.userservice.dto.UserRoomAssignmentRequest;
import com.hotelerp.userservice.service.StaffService;
import com.hotelerp.userservice.constant.ServiceConstant;
import com.hotelerp.userservice.service.UserRoomMapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hmsService/v1/housekeeping/staff")
@RequiredArgsConstructor
public class HousekeepingStaffController {

    private final StaffService staffService;
    private final UserRoomMapService userRoomMapService;


    @PostMapping("/saveAssignments")
    public StandardResponse<Void> saveAssignments(@RequestBody UserRoomAssignmentRequest request) {
        return userRoomMapService.syncUserRooms(request);
    }

    @GetMapping(ServiceConstant.GET_HOUSEKEEPING_STAFF)
    public ResponseEntity<StandardResponse<List<StaffDTO>>> getHousekeepingStaff() {
        StandardResponse<List<StaffDTO>> response = staffService.getHousekeepingStaff();
        return ResponseEntity.ok(response);
    }
}
