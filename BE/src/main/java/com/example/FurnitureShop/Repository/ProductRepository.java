package com.example.FurnitureShop.Repository;

import com.example.FurnitureShop.Model.Product;
import com.example.FurnitureShop.Model.Promotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

    Boolean existsByName(String name);

    Page<Product> findAll(Pageable pageable);

    @Query("""
            SELECT p FROM Product p
            WHERE p.maxPrice <= :maxPrice
             AND p.minPrice <= :minPrice
            ORDER BY p.maxPrice
            """)
    Page<Product> getByRangePrice(Pageable pageable,
                                  @Param("minPrice") BigDecimal minPrice,
                                  @Param("maxPrice") BigDecimal maxPrice);

    @Query("""
            SELECT p FROM Product p
              WHERE (:minPrice IS NULL OR p.minPrice >= :minPrice)
                AND (:maxPrice IS NULL OR p.minPrice <= :maxPrice)
                AND (:categoryId IS NULL OR p.category.id = :categoryId)
                AND (:star IS NULL OR p.regardStar >= :star)
                AND (:inStock IS NULL OR p.inStockCount > 0)
            """)
    Page<Product> getProduct(
            Pageable pageable,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("categoryId") Long categoryId,
            @Param("star") Float star,
            @Param("inStock") Boolean inStock
    );

    @Modifying
    @Query("""
            UPDATE Product p
            SET p.regardStar = :avgStar
            WHERE p.id = :productId
            """)
    void updateRatedStarProduct(@Param("productId") Long productId, @Param("avg_star") Double avgStar);
}
