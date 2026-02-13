package com.example.FurnitureShop.Repository;

import com.example.FurnitureShop.Model.PromotionProducts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PromotionProductRepository extends JpaRepository<PromotionProducts,Long> {

    @Query("""
            SELECT p FROM PromotionProducts p
            WHERE p.promotion.id = :promotionId
            """)
    PromotionProducts findByPromotionId(@Param("promotionId") Long promotionId);

    @Query("""
            SELECT p FROM PromotionProducts p
            WHERE p.product.id = :productId
            """)
    List<PromotionProducts> filterByProductId(@Param("productId") Long productId);

    @Query("""
            SELECT pd FROM PromotionProducts pd
            WHERE pd.product.id = :productId
               AND pd.promotion.endDate >= CURRENT_TIMESTAMP
            """)
    PromotionProducts findActivePromotionProductByProductId(@Param("productId") Long productId);
}
