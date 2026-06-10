package com.hotelerp.userservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "room_bookings", indexes = {
        @Index(name = "idx_room_id", columnList = "room_id"),
        @Index(name = "idx_booking_date", columnList = "booking_date")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "guest_name")
    private String guestName;

    @Column(name = "booking_date", nullable = false)
    private LocalDateTime bookingDate;

    @Column(name = "check_in_date")
    private LocalDateTime checkInDate;

    @Column(name = "check_out_date")
    private LocalDateTime checkOutDate;

    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private BookingStatus status;

    public enum BookingStatus {
        CONFIRMED, CHECKED_IN, CHECKED_OUT, CANCELLED
    }
}
