package com.hotelerp.userservice.repository;

import com.hotelerp.userservice.entity.PosBill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PosBillRepository extends JpaRepository<PosBill, Long> {

    /** All non-deleted bills */
    List<PosBill> findByIsDeletedFalse();

    /** All non-deleted bills – paginated */
    Page<PosBill> findByIsDeletedFalse(Pageable pageable);

    /** Bills for a specific outlet (via the linked PosOrder) */
    @Query("SELECT b FROM PosBill b WHERE b.isDeleted = false AND b.order.outlet.id = :outletId")
    List<PosBill> findByOutletId(@Param("outletId") Long outletId);

    /** Bills for a specific outlet – paginated */
    @Query("SELECT b FROM PosBill b WHERE b.isDeleted = false AND b.order.outlet.id = :outletId")
    Page<PosBill> findByOutletId(@Param("outletId") Long outletId, Pageable pageable);

    /** Bills by status code (e.g. SETTLED, OPEN, VOID) */
    @Query("SELECT b FROM PosBill b WHERE b.isDeleted = false AND b.status.code = :statusCode")
    List<PosBill> findByStatusCode(@Param("statusCode") String statusCode);

    /** Check if a bill already exists for a given order */
    Optional<PosBill> findByOrderIdAndIsDeletedFalse(Long orderId);

    /** Bills within date range */
    @Query("SELECT b FROM PosBill b WHERE b.isDeleted = false AND b.createdAt >= :from AND b.createdAt <= :to")
    List<PosBill> findInDateRange(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    /** Generate next bill number - count of all bills (including deleted) */
    @Query("SELECT COUNT(b) FROM PosBill b")
    long countAll();
}
