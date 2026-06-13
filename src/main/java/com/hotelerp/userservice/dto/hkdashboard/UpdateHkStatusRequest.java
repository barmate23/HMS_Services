package com.hotelerp.userservice.dto.hkdashboard;

import lombok.Data;

@Data
public class UpdateHkStatusRequest {
    private Long roomId;
    private Long hkStatusId;
}
