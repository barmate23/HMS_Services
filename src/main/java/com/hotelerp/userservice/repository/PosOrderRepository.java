package com.hotelerp.userservice.repository;

import com.hotelerp.userservice.entity.PosOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PosOrderRepository extends JpaRepository<PosOrder, Long> {
    List<PosOrder> findByOutletId(Long outletId);
    List<PosOrder> findByDiningTableIdAndStatusValue(Long tableId, String statusValue);
    List<PosOrder> findByRoomIdAndStatusValue(Long roomId, String statusValue);

    @Query("SELECT p FROM PosOrder p WHERE p.createdAt >= :startDate AND p.createdAt <= :endDate")
    List<PosOrder> findAllInDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    List<PosOrder> findByStatusCodeInAndIsDeletedFalse(List<String> codes);
}
