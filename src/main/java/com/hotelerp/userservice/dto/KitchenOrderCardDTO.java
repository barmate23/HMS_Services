package com.hotelerp.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KitchenOrderCardDTO {
    private Long id;
    private String orderNumber;
    private String orderType;
    private Long outletId;
    private String outletName;
    private String tableNumber;
    private String roomNumber;
    private String guestName;
    private String serverName;
    private String kotStatus;
    private LocalDateTime createdAt;
    private List<KitchenOrderItemDTO> items;
}
