package com.example.FurnitureShop.DTO.Request.Kafka;

import com.example.FurnitureShop.Model.OrderItem;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemDTO {
    @JsonProperty("variant_id")
    private Long variantId;
    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("productName")
    private String productName;

    @JsonProperty("price")
    private BigDecimal price;

    private BigDecimal total;

    public static OrderItemDTO fromEntity(OrderItem orderItem) {
        return OrderItemDTO.builder()
                .variantId(orderItem.getProductVariant().getId())
                .quantity(orderItem.getQuantity())
                .productName(orderItem.getProductVariant().getProduct().getName())
                .price(orderItem.getProductVariant().getPrice())
                .total(orderItem.getProductVariant().getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())) )
                .build();
    }
}
