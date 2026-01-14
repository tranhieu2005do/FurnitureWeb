package com.example.FurnitureShop.Repository;

import com.example.FurnitureShop.Model.Product;
import com.example.FurnitureShop.Model.Promotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
