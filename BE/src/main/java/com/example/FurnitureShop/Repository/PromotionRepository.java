package com.example.FurnitureShop.Repository;

import com.example.FurnitureShop.Model.Promotion.DiscountType;
import com.example.FurnitureShop.Model.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PromotionRepository extends JpaRepository<Promotion,Long> {

    @Query("""
            SELECT p FROM Promotion p
            WHERE p.code = :code
            """)
    Promotion findByCode(@Param("code") String code);

    @Query("""
            SELECT p FROM Promotion p
            JOIN PromotionProducts pt ON p.id = pt.promotion.id
            WHERE pt.product.id = :productId AND (:active = false OR (:now BETWEEN p.startDate AND p.endDate))
            ORDER BY p.startDate ASC
            """)
    List<Promotion> filterByProduct(@Param("productId") Long productId,
                                    @Param("active") boolean active,
                                    @Param("now")LocalDateTime now);

    @Query("""
            SELECT COUNT(p) > 0
            FROM Promotion p
            JOIN PromotionUsers pu ON pu.promotion.id = p.id
            WHERE p.discountType = 'BIRTHDAY'
              AND YEAR(p.startDate) = :year
              AND p.isPersonal = true
              AND pu.user.id = :userId
            """)
    boolean existsPromotion(@Param("userId") Long userId,@Param("year") int year);

    @Query("""
            SELECT p
            FROM Promotion p
            WHERE CURRENT_TIMESTAMP BETWEEN p.startDate AND p.endDate
              AND p.isPersonal = FALSE
              AND p.discountType <> :type
              AND NOT EXISTS (
                    SELECT 1
                    FROM PromotionUsage pu
                    WHERE pu.promotion.id = p.id
                      AND pu.user.id = :userId
              )
        """)
    List<Promotion> activePromotionExceptType(
            @Param("userId") Long userId,
            @Param("type") DiscountType type);
}
