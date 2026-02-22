package com.example.FurnitureShop.DTO.Response;

import com.example.FurnitureShop.Model.Promotion.DiscountType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplyPromotionResponse {
    @JsonProperty("id")
    private Long promotionId;
    private String code;
    @JsonProperty("product_name")
    private String productName;
    @JsonProperty("type")
    private DiscountType discountType;
    @JsonProperty("discount")
    private BigDecimal discountValue;
}
