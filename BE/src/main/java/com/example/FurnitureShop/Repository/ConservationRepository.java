package com.example.FurnitureShop.Repository;

import com.example.FurnitureShop.Model.Conservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConservationRepository extends JpaRepository<Conservation,Long> {

    @Query("""
            SELECT c FROM Conservation c
            WHERE c.customer.id = :userId
            """)
    Conservation getConservationByUserId(@Param("userId") Long userId);
}
