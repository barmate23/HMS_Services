package com.hotelerp.userservice.repository;

import com.hotelerp.userservice.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b WHERE b.createdAt BETWEEN :startDate AND :endDate AND b.isDeleted = false")
    List<Booking> findAllInDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    List<Booking> findByRoomIdAndIsDeletedFalse(Long roomId);

    List<Booking> findByReservationId(Long reservationId);

    @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId AND b.isDeleted = false AND :date >= b.checkInDate AND :date < b.checkOutDate")
    java.util.Optional<Booking> findActiveByRoomAndDate(@Param("roomId") Long roomId, @Param("date") java.time.LocalDate date);
}
