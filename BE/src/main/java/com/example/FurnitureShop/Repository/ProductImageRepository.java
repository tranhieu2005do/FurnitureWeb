package com.example.FurnitureShop.Repository;

import com.example.FurnitureShop.Model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage,Long> {

    @Query("""
            SELECT im FROM ProductImage im
            WHERE im.productVariant.id = :id
            """)
    List<ProductImage> findByVariantId(@Param("id") Long variantId);
}
