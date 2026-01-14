package com.example.FurnitureShop.Repository;

import com.example.FurnitureShop.Model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating,Long> {

    @Query("""
            SELECT r FROM Rating r
            WHERE r.product.id = :productId
             AND (:star IS NULL or r.star = :star)
             AND (:haveComments = false
                  OR (r.comments IS NOT NULL AND TRIM(r.comments) <> '')
                  )
            ORDER BY r.createdAt DESC
            """)
    List<Rating> filterByProduct(@Param("productId") Long productId,
                                 @Param("star") Integer star,
                                 @Param("haveComments") boolean haveComments);
}

