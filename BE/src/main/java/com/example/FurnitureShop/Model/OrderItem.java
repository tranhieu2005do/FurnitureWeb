package com.example.FurnitureShop.Model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id")
    private ProductVariant productVariant;

    @Column(name = "price", precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "quantity")
    private Integer quantity;

}
