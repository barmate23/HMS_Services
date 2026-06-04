package com.hotelerp.userservice.repository;

import com.hotelerp.userservice.entity.PosOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PosOrderRepository extends JpaRepository<PosOrder, Long> {
    List<PosOrder> findByOutletId(Long outletId);
    List<PosOrder> findByDiningTableIdAndStatusValue(Long tableId, String statusValue);
    List<PosOrder> findByRoomIdAndStatusValue(Long roomId, String statusValue);
}
