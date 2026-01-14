package com.example.FurnitureShop.DTO.Response;

import com.example.FurnitureShop.Model.CartItem;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemResponse {

    @JsonProperty("variant_id")
    private Long variantId;

    @JsonProperty("quantity")
    private int quantity;

    public static CartItemResponse fromEntity(CartItem cartItem) {
        return CartItemResponse.builder()
                .quantity(cartItem.getQuantity())
                .variantId(cartItem.getProductVariant().getId())
                .build();
    }
}
