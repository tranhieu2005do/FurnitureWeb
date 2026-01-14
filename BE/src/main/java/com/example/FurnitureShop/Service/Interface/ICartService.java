package com.example.FurnitureShop.Service.Interface;

import com.example.FurnitureShop.DTO.Request.CartItemRequest;
import com.example.FurnitureShop.DTO.Response.CartResponse;

public interface ICartService {

    CartResponse getOrCreateCart(Long userId);

    CartResponse addItemToCart(Long userId, CartItemRequest cartItemRequest);

    void removeItemFromCart(Long userId, Long cartItemId);

    void updateCartItems(Long userId, CartItemRequest cartItemRequest);
}
