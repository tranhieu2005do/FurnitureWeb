package com.example.FurnitureShop.Repository;

import com.example.FurnitureShop.Model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Long> {


    @Query("""
            SELECT c FROM CartItem c
            WHERE c.cart.id = :cartId
                 AND c.productVariant.id = :variantId
            """)
    CartItem findByCartItemIdAndVariantId (@Param("cartId") Long cartId,
                                           @Param("variantId") Long variantId);
}
