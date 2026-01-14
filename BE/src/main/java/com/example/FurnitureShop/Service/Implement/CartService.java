package com.example.FurnitureShop.Service.Implement;

import com.example.FurnitureShop.DTO.Request.CartItemRequest;
import com.example.FurnitureShop.DTO.Response.CartResponse;
import com.example.FurnitureShop.Exception.AuthException;
import com.example.FurnitureShop.Exception.BussinessException;
import com.example.FurnitureShop.Exception.NotFoundException;
import com.example.FurnitureShop.Model.Cart;
import com.example.FurnitureShop.Model.CartItem;
import com.example.FurnitureShop.Model.ProductVariant;
import com.example.FurnitureShop.Model.User;
import com.example.FurnitureShop.Repository.CartItemRepository;
import com.example.FurnitureShop.Repository.CartRepository;
import com.example.FurnitureShop.Repository.ProductVariantRepository;
import com.example.FurnitureShop.Repository.UserRepository;
import com.example.FurnitureShop.Service.Interface.ICartService;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Transactional
@CacheConfig(cacheNames = "carts")
public class CartService implements ICartService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    @CacheEvict(allEntries = true)
    public CartResponse getOrCreateCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng với id " + userId));
        Cart cart = cartRepository.findByUserId(userId);
        if(cart == null) {
            Cart newCart = Cart.builder()
                    .user(user)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            cartRepository.save(newCart);
            return CartResponse.fromEntity(newCart);
        }
        return CartResponse.fromEntity(cart);
    }

    @Override
    @Transactional
    @CachePut(key = "#userId")
    public CartResponse addItemToCart(Long userId, CartItemRequest cartItemRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng với id " + userId));
        Cart cart = cartRepository.findByUserId(userId);
        ProductVariant variant = productVariantRepository.findById(cartItemRequest.getVariantId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy sản phẩm có với id " + cartItemRequest.getVariantId()));
        boolean check = false;
        for (CartItem item : cart.getCartItems()) {
            if(item.getProductVariant().getId() == variant.getId()) {
                item.setQuantity(item.getQuantity() + cartItemRequest.getQuantity());
                check = true;
            }
        }
        if(!check){
            CartItem newCartItem = CartItem.builder()
                    .cart(cart)
                    .productVariant(variant)
                    .quantity(cartItemRequest.getQuantity())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            cart.getCartItems().add(newCartItem);
            cartItemRepository.save(newCartItem);
        }
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
        return CartResponse.fromEntity(cart);
    }

    @Override
    @Transactional
    @CacheEvict(key = "#userId")
    public void removeItemFromCart(Long userId, Long cartItemId) {
        Cart cart = cartRepository.findByUserId(userId);
        boolean removed = cart.getCartItems()
                .removeIf(item -> item.getId().equals(cartItemId));

        if (!removed) {
            throw new AuthException("CartItem không tồn tại trong giỏ hàng");
        }

        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    @CacheEvict(key = "#userId")
    public void updateCartItems(Long userId, CartItemRequest cartItemRequest) {
        Cart cart = cartRepository.findByUserId(userId);

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item ->
                        item.getProductVariant().getId()
                                == cartItemRequest.getVariantId())
                .findFirst()
                .orElseThrow(() ->
                        new AuthException("Sản phẩm không tồn tại trong giỏ hàng"));

        int newQuantity = cartItemRequest.getQuantity();
        if (newQuantity <= 0) {
            throw new BussinessException("Số lượng phải lớn hơn 0");
        }

        cartItem.setQuantity(newQuantity);
        cartItem.setUpdatedAt(LocalDateTime.now());

        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }
}
