package com.example.FurnitureShop.DTO.Response;

import com.example.FurnitureShop.Model.CartItem;
import com.example.FurnitureShop.Model.ProductVariant.Color;
import com.example.FurnitureShop.Model.ProductVariant.Material;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedResponse {

    // User information
    private String customerName;
    private String address;
    private String email;
    private String phone;
    @JsonProperty("track_number")
    private String trackingNumber;
    private String note;

    // Staff information;
    private String staffName;

    // Order infomation
    private List<Item> items;
    private BigDecimal subTotal;
    private BigDecimal finalPrice;
    private BigDecimal totalDiscount;

    @Builder
    @Data
    public static class Item{
        @JsonProperty("product_name")
        private String productName;
        private Integer quantity;
        private BigDecimal price;
        private BigDecimal length;
        private BigDecimal width;
        private BigDecimal height;
        private Material material;
        private Color color;

        public static Item fromCartItem(CartItem cartItem){
            return Item.builder()
                    .productName(cartItem.getProductVariant().getProduct().getName())
                    .quantity(cartItem.getQuantity())
                    .price(cartItem.getProductVariant().getPrice())
                    .length(cartItem.getProductVariant().getLength())
                    .width(cartItem.getProductVariant().getWidth())
                    .height(cartItem.getProductVariant().getHeight())
                    .material(cartItem.getProductVariant().getMaterial())
                    .color(cartItem.getProductVariant().getColor())
                    .build();
        }
    }

    // Voucher information;
    private List<ApplyPromotionResponse>  applyPromotion;
}
