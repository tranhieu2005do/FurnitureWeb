package com.example.FurnitureShop.Repository;

import com.example.FurnitureShop.Model.InventoryLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryLogRepository extends JpaRepository<InventoryLog,Long> {

    @Query("""
        SELECT l FROM InventoryLog l
        WHERE (:userId IS NULL OR l.staff.id = :userId)
          AND (:productId IS NULL OR l.productVariant.id = :productId)
        ORDER BY l.createdAt DESC
    """)
    List<InventoryLog> findByFilters(
            @Param("userId") Long userId,
            @Param("productId") Long productId
    );

    @Query("""
        SELECT l FROM InventoryLog l
        WHERE (:userId IS NULL OR l.staff.id = :userId)
          AND (:productId IS NULL OR l.productVariant.id = :productId)
        ORDER BY l.createdAt DESC
    """)
    Page<InventoryLog> findByFilters(
            @Param("userId") Long userId,
            @Param("productId") Long productId,
            Pageable pageable
    );

    @Query("""
        SELECT l FROM InventoryLog l
        WHERE (:userId IS NULL OR l.staff.id = :userId)
          AND (:productId IS NULL OR l.productVariant.id = :productId)
          AND l.createdAt BETWEEN :from AND :to
        ORDER BY l.createdAt DESC
    """)
    List<InventoryLog> findByFiltersAndDate(
            @Param("userId") Long userId,
            @Param("productId") Long productId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );


    @Query("""
    SELECT i 
    FROM InventoryLog i 
    WHERE i.productVariant.product.id = :productId 
      AND i.staff.id = :userId
""")
    List<InventoryLog> getLogsByUserIdandProductId(
            @Param("userId") Long userId,
            @Param("productId") Long productId
    );

}
