package com.hotelerp.userservice.scheduler;

import com.hotelerp.userservice.entity.CommonMaster;
import com.hotelerp.userservice.entity.Room;
import com.hotelerp.userservice.entity.RoomAuditLog;
import com.hotelerp.userservice.entity.SOPCheckpoint;
import com.hotelerp.userservice.entity.User;
import com.hotelerp.userservice.entity.UserRoomMap;
import com.hotelerp.userservice.repository.CommonMasterRepository;
import com.hotelerp.userservice.repository.RoomAuditLogRepository;
import com.hotelerp.userservice.repository.RoomRepository;
import com.hotelerp.userservice.repository.SOPCheckpointRepository;
import com.hotelerp.userservice.repository.UserRoomMapRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoomAuditScheduler {

    private final SOPCheckpointRepository sopCheckpointRepository;
    private final RoomRepository roomRepository;
    private final RoomAuditLogRepository roomAuditLogRepository;
    private final UserRoomMapRepository userRoomMapRepository;
    private final CommonMasterRepository commonMasterRepository;

    /**
     * Runs every day at 12:01 AM to generate audit logs based on SOP frequency.
     */
    @Scheduled(cron = "0 1 0 * * *")
    @Transactional
    public void scheduleAuditLogs() {
        LocalDate today = LocalDate.now();
        log.info("Starting scheduled room audit log generation for {}", today);

        // 1. Process Daily SOPs
        generateLogsForFrequency("DAILY");

        // 2. Process Weekly SOPs (Every Monday)
        if (today.getDayOfWeek() == DayOfWeek.MONDAY) {
            generateLogsForFrequency("WEEKLY");
        }

        // 3. Process Monthly SOPs (1st of every month)
        if (today.getDayOfMonth() == 1) {
            generateLogsForFrequency("MONTHLY");
        }

        log.info("Finished scheduled room audit log generation for {}", today);
    }

    private void generateLogsForFrequency(String frequencyCode) {
        log.debug("Generating audit logs for frequency: {}", frequencyCode);
        List<SOPCheckpoint> checkpoints = sopCheckpointRepository.findByFrequencyCode(frequencyCode);
        if (checkpoints.isEmpty()) {
            return;
        }

        List<Room> activeRooms = roomRepository.findByIsDeletedFalse();
        CommonMaster pendingStatus = commonMasterRepository.findByCategoryAndCode("AUDIT_STATUS", "PENDING")
                .orElse(null);

        for (Room room : activeRooms) {
            User inspector = userRoomMapRepository.findByRoomId(room.getId())
                    .map(UserRoomMap::getUser)
                    .orElse(null);

            for (SOPCheckpoint checkpoint : checkpoints) {
                RoomAuditLog auditLog = RoomAuditLog.builder()
                        .room(room)
                        .checkpoint(checkpoint)
                        .status(pendingStatus)
                        .inspector(inspector)
                        .auditDate(LocalDateTime.now())
                        .build();
                roomAuditLogRepository.save(auditLog);
            }
        }
        log.info("Created {} audit logs for {} rooms with frequency {}", 
                checkpoints.size() * activeRooms.size(), activeRooms.size(), frequencyCode);
    }
}
