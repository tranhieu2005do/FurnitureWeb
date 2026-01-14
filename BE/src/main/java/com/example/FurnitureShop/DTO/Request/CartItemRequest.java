package com.example.FurnitureShop.DTO.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor @NoArgsConstructor
@Builder
public class CartItemRequest {

    @JsonProperty("cart_id")
    @NotBlank(message = "Cần biết mã giỏ hàng.")
    private Long cartId;

    @JsonProperty("variant_id")
    @NotBlank(message = "Cần biết mã sản phẩm")
    private Long variantId;

    @NotBlank(message = "Số lượng sản phẩm mong muốn là bao nhiêu.")
    @JsonProperty("quantity")
    private int quantity;
}
