package com.hotelerp.userservice.repository;

import com.hotelerp.userservice.entity.RoomBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RoomBookingRepository extends JpaRepository<RoomBooking, Long> {

    @Query("SELECT b FROM RoomBooking b WHERE b.bookingDate >= :startDate AND b.bookingDate <= :endDate")
    List<RoomBooking> findAllInDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
