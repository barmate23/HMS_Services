package com.hotelerp.userservice.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FloorStatDTO {
    private Long floorId;
    private String floorName;
    private int total;
    private int available;
    private int occupied;
    private int booked;
    private int bookedRooms;
    private int blocked;
    private int underMaintenanceRooms;
}
