package com.hotelerp.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TableReservationDTO {
    private Long id;
    private Long tableId;
    private String tableNumber;
    private String guestName;
    private Integer covers;
    private Long serverId;
    private String serverName;
    private String bookingTime;
    private Long statusId;
    private String statusValue;
}
