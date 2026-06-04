package com.hotelerp.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PosOrderDTO {
    private Long id;
    private Long outletId;
    private String outletName;
    private Long orderTypeId;
    private String orderTypeName;
    private Long tableId;
    private String tableNumber;
    private Long roomId;
    private String roomNumber;
    private String guestName;
    private Long serverId;
    private String serverName;
    private Integer covers;
    private Long statusId;
    private String statusName;
    private String notes;
    private BigDecimal totalAmount;
    private List<PosOrderItemDTO> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
