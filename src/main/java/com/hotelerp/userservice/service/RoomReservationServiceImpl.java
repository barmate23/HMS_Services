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
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomReservationServiceImpl implements RoomReservationService {

    private final BookingRepository bookingRepository;
    private final RoomRepository    roomRepository;

    /**
     * Finds the active Booking for today by looking up the room ID in the Booking
     * table and matching checkInDate & checkOutDate from the linked Reservation table.
     *
     * @Transactional ensures all LAZY-loaded associations (Reservation, Guest,
     * RoomType, Floor, statuses) can be safely navigated.
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

            // 2. Find active booking for today where date is between Reservation's checkInDate & checkOutDate
            List<Booking> activeBookings = bookingRepository.findActiveByRoomAndDate(roomId, today);

            // 3. Fallback: If no date-matched booking, find the latest booking record for this room
            if (activeBookings.isEmpty()) {
                activeBookings = bookingRepository.findLatestBookingsByRoomId(roomId);
            }

            if (activeBookings.isEmpty()) {
                return StandardResponse.error(
                        "No active reservation found for room id: " + roomId,
                        "NO_ACTIVE_RESERVATION",
                        "There is no active reservation for roomId = " + roomId);
            }

            Booking booking = activeBookings.get(0);
            Reservation reservation = booking.getReservation();
            Guest guest = (reservation != null) ? reservation.getGuest() : null;

            // 4. Map details to DTO
            RoomReservationDTO dto = RoomReservationDTO.builder()
                    // Booking / Reservation IDs
                    .bookingId(booking.getId())
                    .reservationId(reservation != null ? reservation.getId() : null)

                    // Room Info (from Booking -> Room)
                    .roomId(booking.getRoom() != null ? booking.getRoom().getId() : roomId)
                    .roomNumber(booking.getRoom() != null ? booking.getRoom().getRoomNumber() : null)
                    .roomType(booking.getRoom() != null && booking.getRoom().getRoomType() != null
                            ? booking.getRoom().getRoomType().getName() : null)
                    .floorName(booking.getRoom() != null && booking.getRoom().getFloor() != null
                            ? booking.getRoom().getFloor().getFloorNumber() : null)

                    // Guest Info
                    .guestId(guest != null ? guest.getId() : null)
                    .guestName(buildFullName(guest))
                    .guestPhone(guest != null ? guest.getPhone() : null)
                    .guestEmail(guest != null ? guest.getEmail() : null)
                    .guestNationality(guest != null ? guest.getNationality() : null)
                    .isVip(guest != null ? guest.getIsVip() : false)

                    // Stay Info (Check-in & Check-out dates from Reservation entity)
                    .checkInDate(reservation != null && reservation.getCheckInDate() != null
                            ? reservation.getCheckInDate() : booking.getCheckInDate())
                    .checkInTime(reservation != null ? reservation.getCheckInTime() : null)
                    .checkOutDate(reservation != null && reservation.getCheckOutDate() != null
                            ? reservation.getCheckOutDate() : booking.getCheckOutDate())
                    .checkOutTime(reservation != null ? reservation.getCheckOutTime() : null)
                    .numberOfNights(booking.getNumberOfNights() != null ? booking.getNumberOfNights()
                            : (reservation != null ? reservation.getNumberOfNights() : null))
                    .numberOfAdults(reservation != null ? reservation.getNumberOfAdults() : null)
                    .numberOfChildren(reservation != null ? reservation.getNumberOfChildren() : 0)
                    .totalGuests(reservation != null ? reservation.getTotalGuests() : null)

                    // Rate Info
                    .ratePlanName(reservation != null && reservation.getRatePlan() != null
                            ? reservation.getRatePlan().getName() : null)
                    .ratePerNight(booking.getRatePerNight())
                    .ratePlanCharge(booking.getRatePlanCharge())
                    .totalPrice(booking.getTotalPrice())
                    .discountPercentage(booking.getDiscountPercentage())
                    .discountAmount(booking.getDiscountAmount())
                    .finalPrice(booking.getFinalPrice())

                    // Billing Info
                    .billingName(reservation != null ? reservation.getBillingName() : null)
                    .billingMode(reservation != null ? reservation.getBillingMode() : null)
                    .gstNumber(reservation != null ? reservation.getGstNumber() : null)
                    .organisationName(reservation != null ? reservation.getOrganisationName() : null)
                    .travelAgentName(reservation != null ? reservation.getTravelAgentName() : null)
                    .businessSource(reservation != null ? reservation.getBusinessSource() : null)
                    .marketSegment(reservation != null ? reservation.getMarketSegment() : null)
                    .bookingReference(reservation != null ? reservation.getBookingReference() : null)

                    // Status
                    .bookingStatus(booking.getBookingStatus() != null
                            ? booking.getBookingStatus().getValue() : null)
                    .reservationStatus(reservation != null && reservation.getReservationStatus() != null
                            ? reservation.getReservationStatus().getValue() : null)

                    // Notes
                    .specialRequests(reservation != null ? reservation.getSpecialRequests() : null)
                    .notes(reservation != null ? reservation.getNotes() : null)
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
        if (guest == null) return null;
        StringBuilder sb = new StringBuilder();
        if (guest.getTitle() != null) {
            sb.append(guest.getTitle().name()).append(" ");
        }
        if (guest.getFirstName() != null) {
            sb.append(guest.getFirstName()).append(" ");
        }
        if (guest.getLastName() != null) {
            sb.append(guest.getLastName());
        }
        return sb.toString().trim();
    }
}
