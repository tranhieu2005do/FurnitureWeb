package com.example.FurnitureShop.Repository;

import com.example.FurnitureShop.Model.PromotionUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PromotionUsageRepository extends JpaRepository<PromotionUsage, Long> {

    @Query("""
            SELECT 1
            FROM PromotionUsage p
            WHERE p.user.id = :userId AND p.promotion.id = :promotionId
            LIMIT 1
            """)
    boolean existsByPromotionIdAndUserId(@Param("promotionId") Long promotionId,
                                         @Param("userId") Long userId);
}
