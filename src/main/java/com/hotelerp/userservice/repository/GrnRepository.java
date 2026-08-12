package com.hotelerp.userservice.repository;

import com.hotelerp.userservice.entity.Grn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GrnRepository extends JpaRepository<Grn, Long> {
    List<Grn> findByIsDeletedFalse();
    List<Grn> findByHotel_IdAndIsDeletedFalse(Long hotelId);
    Optional<Grn> findByIdAndIsDeletedFalse(Long id);
    
    @org.springframework.data.jpa.repository.Query("SELECT SUM(g.acceptedValue) FROM Grn g WHERE g.isDeleted = false AND (:hotelId IS NULL OR g.hotel.id = :hotelId)")
    java.math.BigDecimal sumAcceptedValueByIsDeletedFalse(@org.springframework.data.repository.query.Param("hotelId") Long hotelId);
}
