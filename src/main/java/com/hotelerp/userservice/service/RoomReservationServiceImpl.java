package com.hotelerp.userservice.service;

import com.hotelerp.userservice.common.StandardResponse;
import com.hotelerp.userservice.dto.RoomReservationDTO;
import com.hotelerp.userservice.entity.Booking;
import com.hotelerp.userservice.entity.Guest;
import com.hotelerp.userservice.entity.Reservation;
import com.hotelerp.userservice.repository.BookingRepository;
import com.hotelerp.userservice.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomReservationServiceImpl implements RoomReservationService {

    private final BookingRepository bookingRepository;
    private final RoomRepository    roomRepository;

    /**
     * Finds the active Booking for today via the already-proven
     * BookingRepository.findActiveByRoomAndDate query, then navigates
     * to Reservation and Guest through the loaded entity graph.
     *
     * @Transactional is required because Room, RoomType, Floor, Reservation,
     * Guest, bookingStatus, and reservationStatus are all LAZY-fetched.
     */
    @Override
    @Transactional(readOnly = true)
    public StandardResponse<RoomReservationDTO> getActiveReservationByRoomId(Long roomId) {
        try {
            // 1. Validate room exists
            if (!roomRepository.existsById(roomId)) {
                return StandardResponse.error(
                        "Room not found with id: " + roomId,
                        "ROOM_NOT_FOUND",
                        "No room record exists for roomId = " + roomId);
            }

            LocalDate today = LocalDate.now();

            // 2. Find the active booking line for this room on today's date.
            //    The query: checkInDate <= today < checkOutDate AND isDeleted = false
            Optional<Booking> bookingOpt =
                    bookingRepository.findActiveByRoomAndDate(roomId, today);

            if (bookingOpt.isEmpty()) {
                return StandardResponse.error(
                        "No active reservation found for room id: " + roomId,
                        "NO_ACTIVE_RESERVATION",
                        "There is no active check-in on " + today + " for roomId = " + roomId);
            }

            Booking     booking     = bookingOpt.get();
            Reservation reservation = booking.getReservation();   // LAZY – safe inside @Transactional
            Guest       guest       = reservation.getGuest();      // LAZY – safe inside @Transactional

            // 3. Map to DTO
            RoomReservationDTO dto = RoomReservationDTO.builder()
                    // Booking / Reservation IDs
                    .bookingId(booking.getId())
                    .reservationId(reservation.getId())

                    // Room
                    .roomId(booking.getRoom().getId())
                    .roomNumber(booking.getRoom().getRoomNumber())
                    .roomType(booking.getRoom().getRoomType() != null
                            ? booking.getRoom().getRoomType().getName() : null)
                    .floorName(booking.getRoom().getFloor() != null
                            ? booking.getRoom().getFloor().getFloorNumber() : null)

                    // Guest
                    .guestId(guest.getId())
                    .guestName(buildFullName(guest))
                    .guestPhone(guest.getPhone())
                    .guestEmail(guest.getEmail())
                    .guestNationality(guest.getNationality())
                    .isVip(guest.getIsVip())

                    // Stay
                    .checkInDate(booking.getCheckInDate())
                    .checkInTime(reservation.getCheckInTime())
                    .checkOutDate(booking.getCheckOutDate())
                    .checkOutTime(reservation.getCheckOutTime())
                    .numberOfNights(booking.getNumberOfNights())
                    .numberOfAdults(reservation.getNumberOfAdults())
                    .numberOfChildren(reservation.getNumberOfChildren())
                    .totalGuests(reservation.getTotalGuests())

                    // Rate
                    .ratePlanName(reservation.getRatePlan() != null
                            ? reservation.getRatePlan().getName() : null)
                    .ratePerNight(booking.getRatePerNight())
                    .ratePlanCharge(booking.getRatePlanCharge())
                    .totalPrice(booking.getTotalPrice())
                    .discountPercentage(booking.getDiscountPercentage())
                    .discountAmount(booking.getDiscountAmount())
                    .finalPrice(booking.getFinalPrice())

                    // Billing
                    .billingName(reservation.getBillingName())
                    .billingMode(reservation.getBillingMode())
                    .gstNumber(reservation.getGstNumber())
                    .organisationName(reservation.getOrganisationName())
                    .travelAgentName(reservation.getTravelAgentName())
                    .businessSource(reservation.getBusinessSource())
                    .marketSegment(reservation.getMarketSegment())
                    .bookingReference(reservation.getBookingReference())

                    // Status
                    .bookingStatus(booking.getBookingStatus() != null
                            ? booking.getBookingStatus().getValue() : null)
                    .reservationStatus(reservation.getReservationStatus() != null
                            ? reservation.getReservationStatus().getValue() : null)

                    // Notes
                    .specialRequests(reservation.getSpecialRequests())
                    .notes(reservation.getNotes())
                    .build();

            return StandardResponse.success(dto, "Active reservation details fetched successfully");

        } catch (Exception e) {
            return StandardResponse.error(
                    "Failed to fetch reservation details",
                    "INTERNAL_SERVER_ERROR",
                    e.getMessage());
        }
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private String buildFullName(Guest guest) {
        StringBuilder sb = new StringBuilder();
        if (guest.getTitle() != null) {
            sb.append(guest.getTitle().name()).append(" ");
        }
        sb.append(guest.getFirstName()).append(" ").append(guest.getLastName());
        return sb.toString().trim();
    }
}
