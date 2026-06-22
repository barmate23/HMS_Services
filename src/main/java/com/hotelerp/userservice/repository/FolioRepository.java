package com.hotelerp.userservice.repository;

import com.hotelerp.userservice.entity.Folio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface FolioRepository extends JpaRepository<Folio, Long> {
    Optional<Folio> findByFolioNumber(String folioNumber);
    Optional<Folio> findByReservationIdAndIsDeletedFalse(Long reservationId);

    @org.springframework.data.jpa.repository.Query("SELECT f FROM Folio f JOIN f.reservation r WHERE f.isDeleted = false AND :today BETWEEN r.checkInDate AND r.checkOutDate")
    java.util.List<Folio> findActiveByDate(@org.springframework.data.repository.query.Param("today") java.time.LocalDate today);
}
