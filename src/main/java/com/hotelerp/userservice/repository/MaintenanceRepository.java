package com.hotelerp.userservice.repository;

import com.hotelerp.userservice.entity.CommonMaster;
import com.hotelerp.userservice.entity.MaintenanceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceRepository extends JpaRepository<MaintenanceRequest, Long> {
    List<MaintenanceRequest> findByStatus(CommonMaster status);
    List<MaintenanceRequest> findByRoomId(Long roomId);
    List<MaintenanceRequest> findByStatusCodeInAndIsDeletedFalse(java.util.List<String> codes);
}
