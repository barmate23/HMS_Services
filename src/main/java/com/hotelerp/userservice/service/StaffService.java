package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.StaffDTO;
import java.util.List;

public interface StaffService {
    StandardResponse<List<StaffDTO>> getHousekeepingStaff();
}
