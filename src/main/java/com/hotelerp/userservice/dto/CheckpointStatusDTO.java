package com.hotelerp.userservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckpointStatusDTO {
    private String checkpointId;
    private String auditArea;
    private String description;
    private String status;
}
