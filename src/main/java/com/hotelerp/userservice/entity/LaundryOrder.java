package com.hotelerp.userservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "laundry_orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LaundryOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", unique = true, nullable = false, length = 20)
    private String orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "guest_name", length = 150)
    private String guestName;

    @Column(name = "service_type", nullable = false, length = 50)
    private String serviceType;

    @Column(name = "billing_option", nullable = false, length = 50)
    private String billingOption;

    @Column(name = "pickup_datetime")
    private LocalDateTime pickupDatetime;

    @Column(name = "expected_delivery")
    private LocalDateTime expectedDelivery;

    @Column(name = "special_instructions", columnDefinition = "TEXT")
    private String specialInstructions;

    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private String status = "PENDING";

    @Column(name = "total_amount")
    private Double totalAmount;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) status = "PENDING";
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
