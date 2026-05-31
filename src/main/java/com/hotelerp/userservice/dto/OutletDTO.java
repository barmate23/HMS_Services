package com.hotelerp.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutletDTO {
    private Long id;
    private String name;
    private Long typeId;
    private String typeValue;
    private String location;
    private String timing;
    private String taxProfile;
    private Long managerId;
    private String managerName;
    private Boolean isActive;
}
