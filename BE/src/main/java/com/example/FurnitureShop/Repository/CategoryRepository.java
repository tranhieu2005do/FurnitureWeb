package com.example.FurnitureShop.Repository;

import com.example.FurnitureShop.Model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {

    @Query("SELECT c FROM Category c WHERE c.isActive = true")
    List<Category> getActiveCategories();

    @Query("SELECT c FROM Category c WHERE c.parentCategory.id IS NULL")
    List<Category> getAllRootCategories();

    Boolean existsByName(String name);

}
