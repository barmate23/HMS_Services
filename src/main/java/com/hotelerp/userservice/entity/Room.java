package com.hotelerp.userservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "rooms", indexes = {
        @Index(name = "idx_floor_id", columnList = "floorId"),
        @Index(name = "idx_room_number", columnList = "roomNumber"),
        @Index(name = "idx_status", columnList = "status_id"),
        @Index(name = "idx_type_id", columnList = "typeId"),
        @Index(name = "idx_hk_status", columnList = "hk_status_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "roomNumber", nullable = false, length = 20)
    private String roomNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "floorId", nullable = false)
    private Floor floor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "typeId", nullable = false)
    private RoomType roomType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private CommonMaster status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hk_status_id")
    private CommonMaster hkStatus;

    @Column(name = "maxOccupancy", nullable = false)
    private Integer maxOccupancy;

    @Column(name = "telephone", length = 20)
    private String telephone;

    @Builder.Default
    @Column(name = "createdAt", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @Column(name = "updatedAt")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Builder.Default
    @Column(name = "isActive")
    private Boolean isActive = true;

    // Enum for Room Status
    public enum RoomStatus {
        VACANT, OCCUPIED, MAINTENANCE, RESERVED, CLEANING
    }
}