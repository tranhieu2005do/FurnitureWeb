package com.example.FurnitureShop.Repository;

import com.example.FurnitureShop.Model.Role;
import com.example.FurnitureShop.Model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByPhone(String phone);

    @Query("""
            SELECT u FROM User u
            JOIN FETCH u.role
            WHERE u.phone = :phone
            """)
    Optional<User> findByPhoneWithRole(String phone);

    List<User> findAll();

    Page<User> findAll(Pageable pageable);

    Page<User> findByRole(Role role, Pageable pageable);

    @Query("""
            SELECT u FROM User u
            WHERE u.role.id = :id
            ORDER BY u.id
            """)
    List<User> findByRoleId(@Param("id") Long roleId);

    @Query("SELECT u FROM User u WHERE u.isActive = true")
    Page<User> findByActive(Pageable pageable);
    @Query("""
            SELECT u FROM User u
            WHERE (:roleId IS NULL or u.role.id = :roleId)
            AND (:isActive IS NULL or u.isActive = :isActive)
            """)
    Page<User> fromPage(Pageable pageable, @Param("roleId") Integer roleId, @Param("isActive") Boolean isActive);

    boolean existsByEmail(String email);

    @Query("""
            SELECT u FROM User u
            WHERE MONTH(u.dateOfBirth) = :month AND DAY(u.dateOfBirth) = :day
            """)
    List<User> findUsersInBirthday(@Param("month") Integer month,
                                       @Param("day") Integer day);

    @Query("""
            SELECT u FROM User u
            WHERE MONTH(u.dateOfBirth) = :month
            AND DAY(u.dateOfBirth) = :day
            """)
    List<User> findByMonthAndDay(@Param("month") Integer month, @Param("day") Integer day);
}
