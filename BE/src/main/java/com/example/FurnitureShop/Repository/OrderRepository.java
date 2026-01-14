package com.example.FurnitureShop.Repository;

import com.example.FurnitureShop.DTO.Response.OrderResponse;
import com.example.FurnitureShop.Model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId")
    List<Order> findByUserId(@Param("userId") Long userId);

    @Query("""
    SELECT o FROM Order o
    WHERE DATE(o.orderDate) BETWEEN :startDate AND :endDate
""")
    List<Order> findByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );


    @Query("SELECT o FROM Order o WHERE o.staff.id = :shipperId")
    Page<Order> findByShipperId(Pageable pageable, @Param("shipperId") Long shipperId);

    @Query("""
            SELECT o FROM Order o
            WHERE ((:startDate IS NULL AND :endDate IS NULL) OR (o.orderDate BETWEEN :startDate AND :endDate))
            ORDER BY o.orderDate DESC
            """)
    Page<Order> getOrders(Pageable pageable,
                                  @Param("startDate") LocalDate startDate,
                                  @Param("endDate") LocalDate endDate);

    @Query("""
            SELECT o FROM Order o
            WHERE o.user.id = :userId
              AND ((:startDate IS NULL AND :endDate IS NULL) OR (o.orderDate BETWEEN :startDate AND :endDate))
            ORDER BY o.orderDate DESC
            """)
    Page<Order> getOrdersByUserId(
            @Param("userId") Long userId,
            Pageable pageable,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
