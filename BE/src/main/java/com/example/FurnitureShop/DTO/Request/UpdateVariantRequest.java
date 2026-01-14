package com.example.FurnitureShop.DTO.Request;

import com.example.FurnitureShop.Model.ProductVariant.Material;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor @AllArgsConstructor
public class UpdateVariantRequest {

    @NotBlank(message = "Bạn nên thay đổi giá.")
    private BigDecimal price;
    private String color;
    private Material material;
    @JsonProperty("in_stock")
    private Long inStock;
}
