package com.example.FurnitureShop.DTO.Response;

import com.example.FurnitureShop.Model.Cart;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartResponse {

    @JsonProperty("cart_id")
    private Long cartId;

    @JsonProperty("cart_items")
    private List<CartItemResponse> cartItems = new ArrayList<>();

    public static CartResponse fromEntity(Cart cart) {
        return CartResponse.builder()
                .cartId(cart.getId())
                .cartItems(cart.getCartItems()
                        .stream()
                        .map(CartItemResponse::fromEntity)
                        .toList())
                .build();
    }
}
