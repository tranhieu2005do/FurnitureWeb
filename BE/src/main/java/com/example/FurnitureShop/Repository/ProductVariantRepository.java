package com.example.FurnitureShop.Repository;

import com.example.FurnitureShop.Model.ProductVariant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant,Long> {

    @Query("""
            SELECT p FROM ProductVariant p
            WHERE (:color IS NULL OR p.color = :color)
              AND (:material IS NULL OR p.material = :material)
              AND (:length IS NULL OR p.length = :length)
              AND (:height IS NULL OR p.height = :height)
              AND (:width IS NULL OR p.width = :width)
              AND (:inStock IS NULL OR p.inStock > 0)
            """)
    Page<ProductVariant> getVariants(Pageable pageable,
                                     @Param("color") String color,
                                     @Param("length") BigDecimal length,
                                     @Param("height") BigDecimal height,
                                     @Param("width") BigDecimal width,
                                     @Param("material") String material,
                                     @Param("inStock") Boolean inStock);
}
