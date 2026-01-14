package com.example.FurnitureShop.DTO.Response;

import com.example.FurnitureShop.Model.OrderItem;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
public class OrderItemReponse {

    @JsonProperty("variant_id")
    private Long variantId;

    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("price")
    private BigDecimal price;

    public static OrderItemReponse fromEntity(OrderItem orderItem) {
        return OrderItemReponse.builder()
                .variantId(orderItem.getProductVariant().getId())
                .quantity(orderItem.getQuantity())
                .price(orderItem.getPrice())
                .build();
    }
}
