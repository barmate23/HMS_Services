package com.hotelerp.userservice.repository;

import com.hotelerp.userservice.entity.TableReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TableReservationRepository extends JpaRepository<TableReservation, Long> {
    List<TableReservation> findByDiningTableId(Long tableId);
}
