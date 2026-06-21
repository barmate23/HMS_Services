package com.hotelerp.userservice.repository;

import com.hotelerp.userservice.entity.RoomAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomAuditLogRepository extends JpaRepository<RoomAuditLog, Long> {
    List<RoomAuditLog> findByRoomId(Long roomId);
    
    @Query("SELECT r FROM RoomAuditLog r WHERE r.room.id = :roomId ORDER BY r.auditDate DESC")
    List<RoomAuditLog> findLatestByRoomId(Long roomId);

    @Query("SELECT r FROM RoomAuditLog r WHERE r.room.id = :roomId AND r.checkpoint.id = :checkpointId ORDER BY r.auditDate DESC")
    List<RoomAuditLog> findLatestByRoomAndCheckpoint(Long roomId, Long checkpointId);

    @Query("SELECT r FROM RoomAuditLog r WHERE r.auditDate >= :start AND r.auditDate <= :end AND r.status.code IN :statusCodes")
    List<RoomAuditLog> findByAuditDateBetweenAndStatusCodeIn(java.time.LocalDateTime start, java.time.LocalDateTime end, java.util.List<String> statusCodes);
}

