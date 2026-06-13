package com.hotelerp.userservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "lost_found_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LostAndFoundItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "item_description", nullable = false, length = 255)
    private String itemDescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private CommonMaster category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "found_by_id", nullable = false)
    private User foundBy;

    @Column(name = "storage_location", nullable = false, length = 100)
    private String storageLocation;

    @Column(name = "found_date", nullable = false)
    private LocalDate foundDate;

    @Column(name = "guest_name", length = 150)
    private String guestName;

    @Column(name = "guest_contact", length = 50)
    private String guestContact;

    @Column(name = "storage_notes", columnDefinition = "TEXT")
    private String storageNotes;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ItemStatus status = ItemStatus.STORED;

    @Builder.Default
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) status = ItemStatus.STORED;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum ItemStatus {
        STORED, CLAIMED, DISPOSED, DONATED
    }
}
