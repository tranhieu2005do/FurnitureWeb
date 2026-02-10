package com.example.FurnitureShop.Controller;

import com.example.FurnitureShop.DTO.Request.CartItemRequest;
import com.example.FurnitureShop.DTO.Response.ApiResponse;
import com.example.FurnitureShop.DTO.Response.CartResponse;
import com.example.FurnitureShop.Service.Implement.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CacheConfig("carts")
@RequestMapping("/api/v1/carts")
public class CartController {

    private final CartService cartService;

    @PreAuthorize("@userService.isSelf(#userId)")
    @PostMapping("/add_item/{userId}")
    public ResponseEntity<ApiResponse<CartResponse>> addItemToCart(
            @PathVariable Long userId,
            @RequestBody @Valid CartItemRequest cartItemRequest
    ) {
        CartResponse response = cartService.addItemToCart(userId, cartItemRequest);
        return ResponseEntity.ok(ApiResponse.<CartResponse>builder()
                .message("Add item to cart successfully")
                .data(response)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @PreAuthorize("@userService.isSelf(#userId)")
    @PatchMapping("/cartItem/{cartItemId}")
    public ResponseEntity<ApiResponse<Void>> removeItemFromCart(
            @PathVariable Long cartItemId,
            @RequestParam Long userId
    ){
        cartService.removeItemFromCart(userId, cartItemId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Remove item from cart successfully")
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @PreAuthorize("@userService.isSelf(#userId)")
    @PatchMapping("/cartItem")
    public ResponseEntity<ApiResponse<Void>> updateCart(
            @RequestParam Long userId,
            @RequestBody @Valid CartItemRequest cartItemRequest)
    {
        cartService.updateCartItems(userId, cartItemRequest);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Update cart successfully")
                .build());
    }

    @PreAuthorize("@userService.isSelf(#userId))")
    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getUserCart(
            @RequestParam Long userId
    ){
        return ResponseEntity.ok(ApiResponse.<CartResponse>builder()
                .message("Get cart successfully")
                .statusCode(HttpStatus.OK.value())
                .data(cartService.getOrCreateCart(userId))
                .build());
    }
}
