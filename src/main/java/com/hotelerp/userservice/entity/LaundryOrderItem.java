package com.hotelerp.userservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "laundry_order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LaundryOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "laundry_order_id", nullable = false)
    private LaundryOrder laundryOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "price_master_id", nullable = false)
    private LaundryPriceMaster priceMaster;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false)
    private Double unitPrice;

    @Column(name = "total", nullable = false)
    private Double total;

    @Column(name = "notes", length = 255)
    private String notes;
}
