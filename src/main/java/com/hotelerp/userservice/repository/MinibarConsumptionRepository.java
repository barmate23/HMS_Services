package com.hotelerp.userservice.repository;

import com.hotelerp.userservice.entity.MinibarConsumption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MinibarConsumptionRepository extends JpaRepository<MinibarConsumption, Long> {
    List<MinibarConsumption> findByIsDeletedFalse();
    List<MinibarConsumption> findByHotel_IdAndIsDeletedFalse(Long hotelId);
    Optional<MinibarConsumption> findByIdAndIsDeletedFalse(Long id);
    List<MinibarConsumption> findByRoomIdAndIsDeletedFalse(Long roomId);
    List<MinibarConsumption> findByHotel_IdAndRoomIdAndIsDeletedFalse(Long hotelId, Long roomId);
    long countByStatus_CodeAndIsDeletedFalse(String statusCode);
    long countByHotel_IdAndStatus_CodeAndIsDeletedFalse(Long hotelId, String statusCode);
}
