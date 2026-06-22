package com.hotelerp.userservice.repository;

import com.hotelerp.userservice.entity.MinibarConsumption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MinibarConsumptionRepository extends JpaRepository<MinibarConsumption, Long> {
    List<MinibarConsumption> findByIsDeletedFalse();
    Optional<MinibarConsumption> findByIdAndIsDeletedFalse(Long id);
    List<MinibarConsumption> findByRoomIdAndIsDeletedFalse(Long roomId);
    long countByStatus_CodeAndIsDeletedFalse(String statusCode);
}
