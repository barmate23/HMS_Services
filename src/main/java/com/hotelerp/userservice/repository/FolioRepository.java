package com.hotelerp.userservice.repository;

import com.hotelerp.userservice.entity.Folio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FolioRepository extends JpaRepository<Folio, Long> {
    Optional<Folio> findByFolioNumber(String folioNumber);
    Optional<Folio> findByReservationIdAndIsDeletedFalse(Long reservationId);

    @Query("SELECT f FROM Folio f JOIN f.reservation r WHERE f.id = :folioId AND f.isDeleted = false AND (:hotelId IS NULL OR r.hotel.id = :hotelId)")
    Optional<Folio> findByIdAndHotelId(@Param("folioId") Long folioId, @Param("hotelId") Long hotelId);

    @Query("SELECT f FROM Folio f JOIN f.reservation r WHERE r.id = :reservationId AND f.isDeleted = false AND (:hotelId IS NULL OR r.hotel.id = :hotelId)")
    Optional<Folio> findByReservationIdAndHotelId(@Param("reservationId") Long reservationId, @Param("hotelId") Long hotelId);

    @Query("SELECT f FROM Folio f JOIN f.reservation r " +
            "WHERE f.isDeleted = false " +
            "AND f.status.code = 'OPEN' " +
            "AND :today BETWEEN r.checkInDate AND r.checkOutDate " +
            "AND (:hotelId IS NULL OR r.hotel.id = :hotelId)")
    List<Folio> findAllOpenFoliosByHotel(@Param("today") LocalDate today, @Param("hotelId") Long hotelId);

    @Query("SELECT f FROM Folio f JOIN f.reservation r WHERE f.isDeleted = false AND :today BETWEEN r.checkInDate AND r.checkOutDate AND (:hotelId IS NULL OR r.hotel.id = :hotelId)")
    List<Folio> findActiveByDateAndHotelId(@Param("today") LocalDate today, @Param("hotelId") Long hotelId);

    @Query("SELECT f FROM Folio f JOIN f.reservation r WHERE f.isDeleted = false AND (:hotelId IS NULL OR r.hotel.id = :hotelId)")
    List<Folio> findByHotelId(@Param("hotelId") Long hotelId);
}
