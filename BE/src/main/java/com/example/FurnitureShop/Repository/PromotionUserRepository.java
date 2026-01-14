package com.example.FurnitureShop.Repository;

import com.example.FurnitureShop.Model.PromotionUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionUserRepository extends JpaRepository<PromotionUsers,Long> {

    @Query("""
            SELECT p FROM PromotionUsers p
            WHERE p.promotion.id = :promotionId
            """)
    PromotionUsers findByPromotionId(@Param("promotionId") Long promotionId);
}
