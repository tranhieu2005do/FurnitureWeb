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

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("variant")
    private ProductVariantResponse variant;

    @JsonProperty("quantity")
    private int quantity;

    @JsonProperty("id")
    private Long cartItemId;

    public static CartItemResponse fromEntity(CartItem cartItem) {
        return CartItemResponse.builder()
                .cartItemId(cartItem.getId())
                .productName(cartItem.getProductVariant().getProduct().getName())
                .quantity(cartItem.getQuantity())
                .variant(ProductVariantResponse.fromEntity(cartItem.getProductVariant()))
                .build();
    }
}
