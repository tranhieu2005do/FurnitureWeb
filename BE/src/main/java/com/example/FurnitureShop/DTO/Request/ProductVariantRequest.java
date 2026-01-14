package com.example.FurnitureShop.DTO.Request;

import com.example.FurnitureShop.Model.ProductVariant.Material;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Builder @Data @NoArgsConstructor
@AllArgsConstructor
public class ProductVariantRequest {


    private BigDecimal length;

    private BigDecimal height;

    private BigDecimal width;

    @NotBlank(message = "Hãy thiết lập giá cho sản phẩm.")
    private BigDecimal price;

    @JsonProperty("in_stock")
    private Long inStock;

    private String color;

    @NotBlank(message = "Hãy cho tôi biết nguyên liệu mà bạn dùng.")
    private Material material;
}
