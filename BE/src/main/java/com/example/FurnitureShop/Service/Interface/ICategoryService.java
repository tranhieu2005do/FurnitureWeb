package com.example.FurnitureShop.Service.Interface;

import com.example.FurnitureShop.DTO.Request.CategoryRequest;
import com.example.FurnitureShop.DTO.Response.CategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ICategoryService {
    List<CategoryResponse> getActiveCategories();


    List<CategoryResponse> getRootCategories();


    List<CategoryResponse> getSubCategories(Long parentCategoryId);


    CategoryResponse getCategoryById(Long id);


    CategoryResponse createCategory(CategoryRequest request);


    CategoryResponse updateCategory(Long id, CategoryRequest request);


    void deleteCategory(Long id);


    Page<CategoryResponse> getAllCategories(Pageable pageable);
}
