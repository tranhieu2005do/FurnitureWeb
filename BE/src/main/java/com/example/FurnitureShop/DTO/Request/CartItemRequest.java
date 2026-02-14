package com.example.FurnitureShop.DTO.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor @NoArgsConstructor
@Builder
public class CartItemRequest {

    @JsonProperty("cart_id")
    @NotNull(message = "Cần biết mã giỏ hàng.")
    private Long cartId;

    @JsonProperty("variant_id")
    @NotNull(message = "Cần biết mã sản phẩm")
    private Long variantId;

    @JsonProperty("quantity")
    @Min(value = 1, message = "Số lượng phải >= 1")
    private int quantity;
}
