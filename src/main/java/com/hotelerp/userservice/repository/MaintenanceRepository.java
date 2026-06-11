package com.hotelerp.userservice.repository;

import com.hotelerp.common.entity.MaintenanceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceRepository extends JpaRepository<MaintenanceRequest, Long> {
    List<MaintenanceRequest> findByStatus(MaintenanceRequest.MaintenanceStatus status);
    List<MaintenanceRequest> findByRoomId(Long roomId);
}
