package com.hotelerp.userservice.repository;

import com.hotelerp.userservice.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /**
     * Returns the active reservation for the given room whose stay window
     * covers the supplied date (checkInDate <= date < checkOutDate).
     * Looks up through the Booking join table so the room-level assignment
     * is respected.
     */
    @Query("""
            SELECT r FROM Reservation r
            JOIN Booking b ON b.reservation.id = r.id
            WHERE b.room.id   = :roomId
              AND b.isDeleted  = false
              AND r.isDeleted  = false
              AND :date        >= b.checkInDate
              AND :date        <  b.checkOutDate
            ORDER BY b.checkInDate DESC
            """)
    Optional<Reservation> findActiveReservationByRoomId(
            @Param("roomId") Long roomId,
            @Param("date")   LocalDate date);
}

