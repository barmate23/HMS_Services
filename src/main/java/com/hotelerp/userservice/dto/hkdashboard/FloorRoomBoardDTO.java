package com.hotelerp.userservice.dto.hkdashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FloorRoomBoardDTO {
    private String floorName;
    private int roomCount;
    private List<RoomBoardDTO> rooms;
}
