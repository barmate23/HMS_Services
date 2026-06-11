package com.hotelerp.userservice.dto.hkdashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomBoardDTO {
    private String roomNumber;
    private String category;
    private String status; // e.g. "OCCUPIED CLEAN"
    private int tasksCount;
    private int maintenanceCount;
    private int lostFoundCount;
    private int sopChecksCount;
    private String assignedStaff;
    private String statusColor; // for UI
}
