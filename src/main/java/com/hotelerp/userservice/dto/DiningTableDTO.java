package com.hotelerp.userservice.dto;

import com.hotelerp.common.entity.DiningTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiningTableDTO {
    private Long id;
    private Long outletId;
    private String outletName;
    private String tableNumber;
    private Long sectionId;
    private String sectionName;
    private Long statusId;
    private String statusName;
    private Integer covers;
    private Long serverId;
    private String serverName;
    private Long linkedTableId;
    private String linkedTableNumber;
}
