package com.hotelerp.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiningTableWithoutOutletDTO {
    private Long id;
    private String tableNumber;
    private Long sectionId;
    private String sectionName;
    private Long statusId;
    private String statusName;
    private Integer covers;
}
