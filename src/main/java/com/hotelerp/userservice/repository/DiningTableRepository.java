package com.hotelerp.userservice.repository;

import com.hotelerp.userservice.entity.DiningTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiningTableRepository extends JpaRepository<DiningTable, Long> {
    List<DiningTable> findByOutletId(Long outletId);
    List<DiningTable> findByStatus(DiningTable.TableStatus status);
}
