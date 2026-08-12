package com.hotelerp.userservice.repository;

import com.hotelerp.userservice.entity.DiningTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiningTableRepository extends JpaRepository<DiningTable, Long> {
    List<DiningTable> findByOutletId(Long outletId);
    List<DiningTable> findByStatusId(Long statusId);

    List<DiningTable> findByIsDeletedFalse();
    List<DiningTable> findByHotel_IdAndIsDeletedFalse(Long hotelId);
    List<DiningTable> findByHotel_IdAndOutletIdAndIsDeletedFalse(Long hotelId, Long outletId);

    boolean existsByOutletIdAndTableNumberIgnoreCaseAndIsDeletedFalse(Long outletId, String tableNumber);

    boolean existsByOutletIdAndTableNumberIgnoreCaseAndIdNotAndIsDeletedFalse(Long outletId, String tableNumber, Long id);
}
