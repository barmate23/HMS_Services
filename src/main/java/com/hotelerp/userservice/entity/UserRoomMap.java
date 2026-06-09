package com.hotelerp.userservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_room_map", indexes = {
        @Index(name = "idx_user_id", columnList = "userId"),
        @Index(name = "idx_room_id", columnList = "roomId")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRoomMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roomId", nullable = false)
    private Room room;

    @Column(name = "assignedAt", nullable = false)
    private LocalDateTime assignedAt;

    @Column(name = "assignedBy")
    private String assignedBy;

    @PrePersist
    protected void onCreate() {
        if (assignedAt == null) {
            assignedAt = LocalDateTime.now();
        }
    }
}
