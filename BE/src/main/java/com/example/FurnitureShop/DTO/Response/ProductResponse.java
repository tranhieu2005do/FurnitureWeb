package com.example.FurnitureShop.DTO.Response;

import com.example.FurnitureShop.Model.Product;
import com.example.FurnitureShop.Model.PromotionProducts;
import com.example.FurnitureShop.Repository.PromotionProductRepository;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {

    private Long id;

    private String name;

    private String description;

    @JsonProperty("category")
    private String categoryName;

    @JsonProperty("stock")
    private Long inStock;

    @JsonProperty("price")
    private BigDecimal price;

    @JsonProperty("original_price")
    private BigDecimal originalPrice;

    @JsonProperty("is_new")
    private Boolean isNew;

    @JsonProperty("discount")
    private Integer discount;

    @JsonProperty("rated_star")
    private Float rating;

    @JsonProperty("is_active")
    private boolean isActive;

    @JsonProperty("rating_count")
    private Long ratingCount;

    @JsonProperty("purchase_count")
    private int purchaseCount;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
