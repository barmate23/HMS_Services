package com.hotelerp.userservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "pos_order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PosOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private PosOrder order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "price", precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(name = "subtotal", precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Builder.Default
    @Column(name = "ready_quantity")
    private Integer readyQuantity = 0;

    /**
     * Item-level KOT status from CommonMaster (category = "KOT_STATUS").
     * Tracks cooking progress independently per line item.
     * Priority (low → high): KOT_SEND → IN_PROGRESS → KOT_READY
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_kot_status_id")
    private CommonMaster kotStatus;

    @PrePersist
    @PreUpdate
    protected void calculateSubtotal() {
        if (price != null && quantity != null) {
            subtotal = price.multiply(new BigDecimal(quantity));
        }
        if (readyQuantity == null) {
            readyQuantity = 0;
        }
    }
}
