package com.hotelerp.userservice.dto;

import lombok.Data;

@Data
public class CheckpointAuditRequest {
    private Long checkpointId;
    private String status; 
    private String remarks;
}
