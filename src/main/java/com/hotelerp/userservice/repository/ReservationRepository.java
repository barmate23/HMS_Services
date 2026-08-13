package com.hotelerp.userservice.repository;

import com.hotelerp.userservice.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    // Active reservation lookup is handled via BookingRepository.findActiveByRoomAndDate
    // and then navigating booking.getReservation() within a @Transactional service method.
}
