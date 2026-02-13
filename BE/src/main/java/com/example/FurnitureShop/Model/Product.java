package com.example.FurnitureShop.Model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Setter @Getter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "regard_star")
    private Float regardStar;

    @Column(name = "max_price")
    private BigDecimal maxPrice;

    @Column(name = "min_price")
    private BigDecimal minPrice;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "purchase_count")
    private int purchaseCount;

    @Column(name = "instock")
    private Long inStockCount;

    @Column(name = "rating_count")
    private Long ratingCount;

    @OneToMany(mappedBy = "product",  fetch = FetchType.LAZY,  cascade = CascadeType.ALL,  orphanRemoval = true)
    List<ProductVariant> productVariants = new ArrayList<>();

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<Rating> ratings = new ArrayList<>();
}
