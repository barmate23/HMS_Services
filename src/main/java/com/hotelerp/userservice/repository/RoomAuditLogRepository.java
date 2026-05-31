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
}
