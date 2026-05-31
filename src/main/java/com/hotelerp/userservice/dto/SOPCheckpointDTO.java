package com.hotelerp.userservice.dto;

import lombok.Data;

@Data
public class SOPCheckpointDTO {
    private Long id;
    private String checkpointId;
    
    private Long frequencyId;
    private String frequencyValue;
    
    private String auditArea; // Changed back to String as requested
    
    private Long responsibleRoleId;
    private String responsibleRoleValue;
    
    private String description;
}
