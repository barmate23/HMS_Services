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
public class LaundryOrderDTO {
    private Long id;
    private String orderId;
    private Long roomId;
    private String roomNumber;
    private String floorNumber;
    private String guestName;
    private String serviceType;
    private List<String> serviceTypes;
    private String billingOption;
    private LocalDateTime pickupDatetime;
    private LocalDateTime expectedDelivery;
    private String specialInstructions;
    private String status;
    private Double totalAmount;
    private List<LaundryOrderItemDTO> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
