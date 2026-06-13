package com.hotelerp.userservice.dto.laundrydashboard;

import com.hotelerp.userservice.dto.LaundryOrderDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LaundryDashboardActivityDTO {
    private Long id;
    private String orderId;
    private String room;
    private String guest;
    private String status;
    private Integer itemCount;
    private Double amount;
    private String createdAt;

    public static LaundryDashboardActivityDTO fromOrder(LaundryOrderDTO order) {
        return LaundryDashboardActivityDTO.builder()
                .id(order.getId())
                .orderId(order.getOrderId())
                .room(order.getRoomNumber())
                .guest(order.getGuestName())
                .status(order.getStatus())
                .itemCount(order.getItems() != null
                        ? order.getItems().stream().mapToInt(item -> item.getQuantity() != null ? item.getQuantity() : 0).sum()
                        : 0)
                .amount(order.getTotalAmount())
                .createdAt(order.getCreatedAt() != null ? order.getCreatedAt().toString() : null)
                .build();
    }
}
