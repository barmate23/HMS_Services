package com.hotelerp.userservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sop_checkpoints")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SOPCheckpoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "checkpoint_id", nullable = false, length = 20)
    private String checkpointId; // e.g., D01, D02

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "frequency_id", nullable = false)
    private CommonMaster frequency;

    @Column(name = "audit_area", nullable = false)
    private String auditArea; // String field as requested

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsible_role_id", nullable = false)
    private CommonMaster responsibleRole; // e.g., Room Attendant

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
