package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.hkdashboard.*;
import com.hotelerp.userservice.entity.*;
import com.hotelerp.userservice.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HousekeepingDashboardServiceImpl implements HousekeepingDashboardService {

        private final RoomRepository roomRepository;
        private final FloorRepository floorRepository;
        private final TaskRepository taskRepository;
        private final MaintenanceRepository maintenanceRepository;
        private final LostAndFoundRepository lostAndFoundRepository;
        private final RoomAuditLogRepository roomAuditLogRepository;
        private final SOPCheckpointRepository sopCheckpointRepository;
        private final UserRoomMapRepository userRoomMapRepository;
        private final UserRepository userRepository;
        private final CommonMasterRepository commonMasterRepository;

        @Override
        public StandardResponse<HousekeepingDashboardDTO> getHousekeepingDashboardData() {
                try {
                        List<Room> allRooms = roomRepository.findAll();
                        List<Floor> allFloors = floorRepository.findAll();
                        List<Task> allTasks = taskRepository.findAll();
                        List<MaintenanceRequest> allMaintenance = maintenanceRepository.findAll();
                        List<LostAndFoundItem> allLostFound = lostAndFoundRepository.findAll();
                        List<RoomAuditLog> allAuditLogs = roomAuditLogRepository.findAll();
                        List<SOPCheckpoint> allCheckpoints = sopCheckpointRepository.findAll();
                        List<UserRoomMap> roomAssignments = userRoomMapRepository.findAll();

                        // 1. Summary Metrics
                        int readyRooms = (int) allRooms.stream()
                                        .filter(r -> isStatus(r.getHkStatus(), "Vacant clean")
                                                        || isStatus(r.getHkStatus(), "occupied clean"))
                                        .count();
                        int needService = (int) allRooms.stream()
                                        .filter(r -> isStatus(r.getHkStatus(), "Vacant dirty")
                                                        || isStatus(r.getHkStatus(), "occupied dirty"))
                                        .count();
                        int blockedDnd = (int) allRooms.stream()
                                        .filter(r -> isStatus(r.getHkStatus(), "Do not disturb")
                                                        || isStatus(r.getHkStatus(), "under maintenance"))
                                        .count();
                        int openTasksCount = (int) allTasks.stream()
                                        .filter(t -> t.getStatus() != Task.TaskStatus.COMPLETED)
                                        .count();
                        int repairIssues = (int) allMaintenance.stream()
                                        .filter(m -> !isStatus(m.getStatus(), "COMPLETED"))
                                        .count();
                        int sopChecks = (int) allAuditLogs.size();

                        HkSummaryDTO summary = HkSummaryDTO.builder()
                                        .readyRooms(readyRooms)
                                        .needService(needService)
                                        .blockedDnd(blockedDnd)
                                        .openTasks(openTasksCount)
                                        .repairIssues(repairIssues)
                                        .sopChecks(sopChecks)
                                        .readyPercentage(allRooms.size() > 0 ? (readyRooms * 100 / allRooms.size()) : 0)
                                        .build();

                        // 2. Attention Queue
                        List<HkAttentionItemDTO> attentionQueue = new ArrayList<>();
                        attentionQueue.add(HkAttentionItemDTO.builder().label("dirty").count(needService).type("DIRTY")
                                        .build());
                        attentionQueue.add(HkAttentionItemDTO.builder().label("maintenance blockers")
                                        .count(repairIssues).type("MAINTENANCE_BLOCKER").build());
                        attentionQueue.add(HkAttentionItemDTO.builder().label("stored lost & found")
                                        .count((int) allLostFound.stream().filter(
                                                        i -> i.getStatus() == LostAndFoundItem.ItemStatus.STORED)
                                                        .count())
                                        .type("LOST_FOUND").build());

                        // 3. Team Load
                        HkTeamLoadDTO teamLoad = HkTeamLoadDTO.builder()
                                        .pendingSubmissions((int) allTasks.stream()
                                                        .filter(t -> t.getStatus() == Task.TaskStatus.PENDING).count())
                                        .inProgress((int) allTasks.stream()
                                                        .filter(t -> t.getStatus() == Task.TaskStatus.IN_PROGRESS)
                                                        .count())
                                        .staffProfiles((int) userRepository.count()) // simplified
                                        .build();

                        // 4. Audit Readiness
                        HkAuditReadinessDTO auditReadiness = HkAuditReadinessDTO.builder()
                                        .activeSop("DAILY")
                                        .checkpoints(allCheckpoints.size())
                                        .roomsTracked((int) allAuditLogs.stream().map(RoomAuditLog::getRoom).distinct()
                                                        .count())
                                        .build();

                        // 5. Floor Room Board
                        List<FloorRoomBoardDTO> floorRoomBoard = allFloors.stream().map(floor -> {
                                List<Room> roomsOnFloor = allRooms.stream()
                                                .filter(r -> r.getFloor().getId().equals(floor.getId()))
                                                .collect(Collectors.toList());

                                List<RoomBoardDTO> roomBoardDTOs = roomsOnFloor.stream().map(room -> {
                                        String assignedStaff = roomAssignments.stream()
                                                        .filter(map -> map.getRoom().getId().equals(room.getId()))
                                                        .map(map -> map.getUser().getFullName())
                                                        .findFirst().orElse("Unassigned");

                                        String hkStatusVal = room.getHkStatus() != null
                                                        ? room.getHkStatus().getValue().toUpperCase()
                                                        : "N/A";

                                        return RoomBoardDTO.builder()
                                                        .roomNumber(room.getRoomNumber())
                                                        .category(room.getRoomType() != null
                                                                        ? room.getRoomType().getName()
                                                                        : "N/A")
                                                        .status(hkStatusVal)
                                                        .tasksCount((int) allTasks.stream()
                                                                        .filter(t -> t.getRoom() != null && t.getRoom()
                                                                                        .getId().equals(room.getId()))
                                                                        .count())
                                                        .maintenanceCount((int) allMaintenance.stream()
                                                                        .filter(m -> m.getRoom() != null && m.getRoom()
                                                                                        .getId().equals(room.getId()))
                                                                        .count())
                                                        .lostFoundCount((int) allLostFound.stream()
                                                                        .filter(i -> i.getRoom() != null && i.getRoom()
                                                                                        .getId().equals(room.getId()))
                                                                        .count())
                                                        .sopChecksCount((int) allAuditLogs.stream()
                                                                        .filter(a -> a.getRoom() != null && a.getRoom()
                                                                                        .getId().equals(room.getId()))
                                                                        .count())
                                                        .assignedStaff(assignedStaff)
                                                        .statusColor(getStatusColor(room))
                                                        .build();
                                }).collect(Collectors.toList());

                                return FloorRoomBoardDTO.builder()
                                                .floorName(floor.getFloorNumber())
                                                .roomCount(roomsOnFloor.size())
                                                .rooms(roomBoardDTOs)
                                                .build();
                        }).collect(Collectors.toList());

                        HousekeepingDashboardDTO dashboardDTO = HousekeepingDashboardDTO.builder()
                                        .summary(summary)
                                        .attentionQueue(attentionQueue)
                                        .teamLoad(teamLoad)
                                        .auditReadiness(auditReadiness)
                                        .floorRoomBoard(floorRoomBoard)
                                        .build();

                        return StandardResponse.success(dashboardDTO,
                                        "Housekeeping dashboard data fetched successfully");
                } catch (Exception e) {
                        log.error("Error fetching HK dashboard data: ", e);
                        return StandardResponse.error("Failed to fetch HK dashboard data", "INTERNAL_SERVER_ERROR",
                                        e.getMessage());
                }
        }

        @Override
        public StandardResponse<Void> updateRoomHkStatus(UpdateHkStatusRequest request) {
                try {
                        Long roomId = request.getRoomId();
                        Long hkStatusId = request.getHkStatusId();

                        if (roomId == null || hkStatusId == null) {
                                return StandardResponse.error("Room ID and HK Status ID must not be null",
                                                "BAD_REQUEST", null);
                        }

                        Room room = roomRepository.findById(roomId)
                                        .orElseThrow(() -> new RuntimeException("Room not found with ID: " + roomId));

                        CommonMaster hkStatus = commonMasterRepository.findById(hkStatusId)
                                        .orElseThrow(() -> new RuntimeException(
                                                        "Housekeeping status not found with ID: " + hkStatusId));

                        room.setHkStatus(hkStatus);
                        room.setUpdatedAt(java.time.LocalDateTime.now());
                        roomRepository.save(room);

                        log.info("Updated HK status of room {} to {}", room.getRoomNumber(), hkStatus.getValue());
                        return StandardResponse.success(null, "Room housekeeping status updated successfully");
                } catch (Exception e) {
                        log.error("Error updating room HK status: ", e);
                        return StandardResponse.error("Failed to update room housekeeping status",
                                        "INTERNAL_SERVER_ERROR", e.getMessage());
                }
        }

        private boolean isStatus(CommonMaster status, String expected) {
                return status != null && expected.equalsIgnoreCase(status.getValue());
        }

        private String getStatusColor(Room room) {
                if (isStatus(room.getHkStatus(), "CLEAN"))
                        return "green";
                if (isStatus(room.getHkStatus(), "DIRTY"))
                        return "red";
                if (isStatus(room.getHkStatus(), "DND"))
                        return "purple";
                return "gray";
        }
}
