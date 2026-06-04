package com.hotelerp.userservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "menu_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outlet_id", nullable = false)
    private Outlet outlet;

    @Column(name = "item_name", nullable = false, length = 150)
    private String itemName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CommonMaster category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subcategory_id")
    private CommonMaster subcategory;

    @Lob
    @Column(name = "item_image", columnDefinition = "LONGBLOB")
    private byte[] itemImage;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "tax_percent", precision = 5, scale = 2)
    private BigDecimal taxPercent;

    @Column(name = "variants", columnDefinition = "TEXT")
    private String variants;

    @Column(name = "modifiers", columnDefinition = "TEXT")
    private String modifiers;

    @Column(name = "happy_hour_price", precision = 10, scale = 2)
    private BigDecimal happyHourPrice;

    @Column(name = "happy_hour_window", length = 100)
    private String happyHourWindow;

    @Column(name = "linked_stock_item", length = 150)
    private String linkedStockItem;

    @Column(name = "is_available")
    @Builder.Default
    private Boolean isAvailable = true;

    @Column(name = "is_featured")
    @Builder.Default
    private Boolean isFeatured = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isAvailable == null) isAvailable = true;
        if (isFeatured == null) isFeatured = false;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
