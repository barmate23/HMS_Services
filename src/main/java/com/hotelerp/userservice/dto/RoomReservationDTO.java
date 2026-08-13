package com.hotelerp.userservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomReservationDTO {

    // ── Booking Info ─────────────────────────────────────────────────────────
    private Long bookingId;
    private Long reservationId;

    // ── Room Info ─────────────────────────────────────────────────────────────
    private Long roomId;
    private String roomNumber;
    private String roomType;
    private String floorName;

    // ── Guest Info ────────────────────────────────────────────────────────────
    private Long guestId;
    private String guestName;
    private String guestPhone;
    private String guestEmail;
    private String guestNationality;
    private Boolean isVip;

    // ── Stay Info ─────────────────────────────────────────────────────────────
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkInDate;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime checkInTime;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkOutDate;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime checkOutTime;

    private Integer numberOfNights;
    private Integer numberOfAdults;
    private Integer numberOfChildren;
    private Integer totalGuests;

    // ── Rate Info ─────────────────────────────────────────────────────────────
    private String ratePlanName;
    private BigDecimal ratePerNight;
    private BigDecimal ratePlanCharge;
    private BigDecimal totalPrice;
    private BigDecimal discountPercentage;
    private BigDecimal discountAmount;
    private BigDecimal finalPrice;

    // ── Billing Info ──────────────────────────────────────────────────────────
    private String billingName;
    private String billingMode;
    private String gstNumber;
    private String organisationName;
    private String travelAgentName;
    private String businessSource;
    private String marketSegment;
    private String bookingReference;

    // ── Status ────────────────────────────────────────────────────────────────
    private String bookingStatus;
    private String reservationStatus;

    // ── Notes ─────────────────────────────────────────────────────────────────
    private String specialRequests;
    private String notes;
}
