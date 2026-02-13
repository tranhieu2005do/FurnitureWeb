package com.example.FurnitureShop.DTO.Response;

import com.example.FurnitureShop.Model.Product;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor @AllArgsConstructor
public class ProductResponse {

    private Long id;
    private String name;
    private String description;

    @JsonProperty("category_id")
    private Long categoryId;

    @JsonProperty("category_name")
    private String categoryName;

    @JsonProperty("in_stock")
    private Long inStock;

    @JsonProperty("rated_star")
    private Float rating;

    @JsonProperty("is_active")
    private boolean isActive;

    @JsonProperty("rating_count")
    private Long ratingCount;

    @JsonProperty("purchase_count")
    private int purchaseCount;

    private List<ProductVariantResponse> variants;

    public static ProductResponse fromEntity(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .inStock(product.getInStockCount())
                .isActive(product.isActive())
                .ratingCount(product.getRatingCount())
                .purchaseCount(product.getPurchaseCount())
                .rating(product.getRegardStar())
                .variants(product.getProductVariants()
                        .stream()
                        .map(ProductVariantResponse::fromEntity)
                        .toList())
                .build();
    }
}
