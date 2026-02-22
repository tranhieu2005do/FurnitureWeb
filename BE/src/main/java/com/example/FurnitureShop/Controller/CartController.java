package com.example.FurnitureShop.Controller;

import com.example.FurnitureShop.DTO.Request.CartItemRequest;
import com.example.FurnitureShop.DTO.Response.ApiResponse;
import com.example.FurnitureShop.DTO.Response.CartResponse;
import com.example.FurnitureShop.Model.CustomUserDetails;
import com.example.FurnitureShop.Service.Implement.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CacheConfig("carts")
@RequestMapping("/api/v1/carts")
public class CartController {

    private final CartService cartService;

    @PostMapping("/add_item")
    public ResponseEntity<ApiResponse<CartResponse>> addItemToCart(
            @RequestBody @Valid CartItemRequest cartItemRequest
    ) {
        CustomUserDetails customUserDetails = (CustomUserDetails)
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();
        Long userId = customUserDetails.getUserId();

        CartResponse response = cartService.addItemToCart(userId, cartItemRequest);
        return ResponseEntity.ok(ApiResponse.<CartResponse>builder()
                .message("Add item to cart successfully")
                .data(response)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @PatchMapping("/cartItem/{cartItemId}")
    public ResponseEntity<ApiResponse<Void>> removeItemFromCart(
            @PathVariable Long cartItemId
    ){
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Long userId = userDetails.getUserId();
        cartService.removeItemFromCart(userId, cartItemId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Remove item from cart successfully")
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @PatchMapping("/cartItem")
    public ResponseEntity<ApiResponse<Void>> updateCart(
            @RequestBody @Valid CartItemRequest cartItemRequest)
    {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Long userId = userDetails.getUserId();
        cartService.updateCartItems(userId, cartItemRequest);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Update cart successfully")
                .build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getUserCart(){
        // Lấy thông tin user từ token
        CustomUserDetails customUserDetails = (CustomUserDetails)
                SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Long userId = customUserDetails.getUserId();
        return ResponseEntity.ok(ApiResponse.<CartResponse>builder()
                .message("Get cart successfully")
                .statusCode(HttpStatus.OK.value())
                .data(cartService.getOrCreateCart(userId))
                .build());
    }
}
