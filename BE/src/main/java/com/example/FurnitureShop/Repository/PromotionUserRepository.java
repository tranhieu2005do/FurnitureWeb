package com.example.FurnitureShop.Repository;

import com.example.FurnitureShop.Model.PromotionUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PromotionUserRepository extends JpaRepository<PromotionUsers,Long> {

    @Query("""
            SELECT p FROM PromotionUsers p
            WHERE p.promotion.id = :promotionId
               AND p.user.id = :userId
            """)
    PromotionUsers findByPromotionIdAndUserId(@Param("promotionId") Long promotionId,
                                              @Param("userId") Long userId);

    @Query("""
            SELECT p FROM PromotionUsers p
            WHERE CURRENT_TIMESTAMP BETWEEN p.promotion.startDate AND p.promotion.endDate
               AND p.user.id = :id
               AND p.usedAt IS NULL
            """)
    List<PromotionUsers> findPromotionActiveByUserId(@Param("id") Long userId);
}
