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
       List<PosOrder> findAllInDateRange(@Param("startDate") LocalDateTime startDate,
                     @Param("endDate") LocalDateTime endDate);

       List<PosOrder> findByStatusCodeInAndIsDeletedFalse(List<String> codes);

       List<PosOrder> findByDiningTableIdAndStatusCodeInAndIsDeletedFalse(Long tableId, List<String> codes);

       List<PosOrder> findByOutletIdAndStatusCodeInAndIsDeletedFalse(Long outletId, List<String> codes);

       List<PosOrder> findByHotel_IdAndIsDeletedFalse(Long hotelId);

       List<PosOrder> findByHotel_IdAndOutletIdAndIsDeletedFalse(Long hotelId, Long outletId);

       List<PosOrder> findByHotel_IdAndStatusCodeInAndIsDeletedFalse(Long hotelId, List<String> codes);

       List<PosOrder> findByHotel_IdAndDiningTableIdAndStatusCodeInAndIsDeletedFalse(Long hotelId, Long tableId, List<String> codes);

       List<PosOrder> findByHotel_IdAndOutletIdAndStatusCodeInAndIsDeletedFalse(Long hotelId, Long outletId, List<String> codes);

       @Query("SELECT p FROM PosOrder p WHERE p.isDeleted = false AND " +
                     "(UPPER(p.kotStatus.code) IN :kotStatuses OR UPPER(p.kotStatus.value) IN :kotStatuses)")
       List<PosOrder> findByKotStatusIn(@Param("kotStatuses") List<String> kotStatuses);

       @Query("SELECT p FROM PosOrder p WHERE p.isDeleted = false AND (:hotelId IS NULL OR p.hotel.id = :hotelId) AND " +
                     "(UPPER(p.kotStatus.code) IN :kotStatuses OR UPPER(p.kotStatus.value) IN :kotStatuses)")
       List<PosOrder> findByHotel_IdAndKotStatusIn(@Param("hotelId") Long hotelId, @Param("kotStatuses") List<String> kotStatuses);

       @Query("SELECT p FROM PosOrder p WHERE p.isDeleted = false AND p.outlet.id = :outletId AND " +
                     "(UPPER(p.kotStatus.code) IN :kotStatuses OR UPPER(p.kotStatus.value) IN :kotStatuses)")
       List<PosOrder> findByOutletIdAndKotStatusIn(@Param("outletId") Long outletId,
                     @Param("kotStatuses") List<String> kotStatuses);

       @Query("SELECT p FROM PosOrder p WHERE p.isDeleted = false AND (:hotelId IS NULL OR p.hotel.id = :hotelId) AND p.outlet.id = :outletId AND " +
                     "(UPPER(p.kotStatus.code) IN :kotStatuses OR UPPER(p.kotStatus.value) IN :kotStatuses)")
       List<PosOrder> findByHotel_IdAndOutletIdAndKotStatusIn(@Param("hotelId") Long hotelId, @Param("outletId") Long outletId,
                     @Param("kotStatuses") List<String> kotStatuses);
}
