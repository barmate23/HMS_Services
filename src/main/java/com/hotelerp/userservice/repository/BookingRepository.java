package com.hotelerp.userservice.repository;

import com.hotelerp.userservice.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b WHERE b.createdAt BETWEEN :startDate AND :endDate AND (b.isDeleted = false OR b.isDeleted IS NULL)")
    List<Booking> findAllInDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    List<Booking> findByRoomIdAndIsDeletedFalse(Long roomId);

    List<Booking> findByReservationId(Long reservationId);

    /**
     * Retrieves active bookings for a room by checking room.id in the Booking table
     * and checkInDate / checkOutDate in the linked Reservation table.
     */
    @Query("""
            SELECT b FROM Booking b 
            JOIN b.reservation r 
            WHERE b.room.id = :roomId 
              AND (b.isDeleted = false OR b.isDeleted IS NULL) 
              AND (r.isDeleted = false OR r.isDeleted IS NULL) 
              AND :date BETWEEN r.checkInDate AND r.checkOutDate 
            ORDER BY r.id DESC
            """)
    List<Booking> findActiveByRoomAndDate(@Param("roomId") Long roomId, @Param("date") LocalDate date);

    /**
     * Retrieves latest non-deleted bookings for a room ordered by reservation check-in date.
     */
    @Query("""
            SELECT b FROM Booking b 
            JOIN b.reservation r 
            WHERE b.room.id = :roomId 
              AND (b.isDeleted = false OR b.isDeleted IS NULL) 
              AND (r.isDeleted = false OR r.isDeleted IS NULL) 
            ORDER BY r.checkInDate DESC, r.id DESC
            """)
    List<Booking> findLatestBookingsByRoomId(@Param("roomId") Long roomId);

    /**
     * Retrieves all active bookings for a hotel where given date falls between reservation check-in and check-out dates.
     */
    @Query("""
            SELECT b FROM Booking b 
            JOIN b.reservation r 
            WHERE (b.isDeleted = false OR b.isDeleted IS NULL) 
              AND (r.isDeleted = false OR r.isDeleted IS NULL) 
              AND :date BETWEEN r.checkInDate AND r.checkOutDate 
              AND (:hotelId IS NULL OR r.hotel.id = :hotelId)
            ORDER BY r.id DESC
            """)
    List<Booking> findAllActiveBookingsByDateAndHotel(@Param("date") LocalDate date, @Param("hotelId") Long hotelId);
}


