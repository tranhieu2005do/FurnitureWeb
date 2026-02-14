package com.example.FurnitureShop.DTO.Response;

import com.example.FurnitureShop.Model.ProductVariant.Material;
import com.example.FurnitureShop.Model.ProductVariant.Color;
import com.example.FurnitureShop.Model.ProductVariant;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Data @AllArgsConstructor @NoArgsConstructor
public class ProductVariantResponse {
    @JsonProperty("id")
    private Long variantId;
    private Long inStock;
    private Long productId;
    private BigDecimal height;
    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal price;
    private Color color;
    private Material material;
    @JsonProperty("is_active")
    private boolean isActive;

    public static  ProductVariantResponse fromEntity(ProductVariant product){
        return ProductVariantResponse.builder()
                .variantId(product.getId())
                .inStock(product.getInStock())
                .productId(product.getProduct().getId())
                .height(product.getHeight())
                .length(product.getLength())
                .width(product.getWidth())
                .price(product.getPrice())
                .color(product.getColor())
                .material(product.getMaterial())
                .isActive(product.isActive())
                .build();
    }

}
