package com.example.FurnitureShop.Service.Implement;

import com.example.FurnitureShop.DTO.Request.CategoryRequest;
import com.example.FurnitureShop.DTO.Response.CategoryResponse;
import com.example.FurnitureShop.Exception.AuthException;
import com.example.FurnitureShop.Exception.NotFoundException;
import com.example.FurnitureShop.Repository.CategoryRepository;
import com.example.FurnitureShop.Model.Category;
import com.example.FurnitureShop.Service.Interface.ICategoryService;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.Cache;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
@AllArgsConstructor
@CacheConfig(cacheNames = "categories")
public class CategoryService implements ICategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(key = "'active'")
    public List<CategoryResponse> getActiveCategories() {
        return categoryRepository.getActiveCategories()
                .stream()
                .map(CategoryResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(key = "'root'")
    public List<CategoryResponse> getRootCategories() {
        return categoryRepository
                .getAllRootCategories()
                .stream()
                .map(CategoryResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(key = "'sub_' + #parentCategoryId")
    public List<CategoryResponse> getSubCategories(Long parentCategoryId) {
        Category category = categoryRepository.findById(parentCategoryId)
                .orElseThrow(() -> new NotFoundException("Không thể tìm thấy danh mục !"));
        List<Category> subCategories = category.getCategories();
        return subCategories.stream()
                .map(CategoryResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(key = "#id")
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));
        return CategoryResponse.fromEntity(category);
    }

    @Override
    @Transactional
    @CacheEvict(allEntries = true)
    public CategoryResponse createCategory(CategoryRequest request) {
        if(categoryRepository.existsByName(request.getName())) {
            throw new AuthException("Category with name " + request.getName() + " already exists");
        }

        Category newCategory = Category.builder()
                .name(request.getName())
                .createdAt(LocalDateTime.now())
                .description(request.getDescription())
                .updatedAt(LocalDateTime.now())
                .isActive(true)
                .build();

        if(request.getParentCategoryId() != 0){
            Category parentCategory = categoryRepository.findById(request.getParentCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category with id " + request.getParentCategoryId() + " not found"));
            newCategory.setParentCategory(parentCategory);
        }

        categoryRepository.save(newCategory);
        return CategoryResponse.fromEntity(newCategory);
    }

    @Override
    @Transactional
    @CacheEvict(allEntries = true)
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category with id " + id + " not found"));
        if(request.getName() != null){
            if(existingCategory.getName() != null && !existingCategory.getName().equals(request.getName()) || existingCategory.getName() == null) {
                if(categoryRepository.existsByName(request.getName())) {
                    throw new AuthException("Category with name " + request.getName() + " already exists");
                }
                existingCategory.setName(request.getName());
            }
        }

        if(request.getDescription() != null) {
            if (existingCategory.getDescription() != null && !existingCategory.getDescription().equals(request.getDescription()) || existingCategory.getDescription() == null) {
                existingCategory.setDescription(request.getDescription());
            }
        }
        if(request.getParentCategoryId() != null) {
            if(existingCategory.getParentCategory() == null || existingCategory.getParentCategory() != null && request.getParentCategoryId() != existingCategory.getParentCategory().getId()) {}
            Category parentCategory = categoryRepository.findById(request.getParentCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category with id " + request.getParentCategoryId() + " not found"));
            if(!parentCategory.isActive()){
                throw new AuthException("Danh mục cha không hoạt động");
            }
            existingCategory.setParentCategory(parentCategory);
            existingCategory.setUpdatedAt(LocalDateTime.now());
        }
        categoryRepository.save(existingCategory);
        return CategoryResponse.fromEntity(existingCategory);
    }

    @Override
    @CacheEvict(allEntries = true)
    public void deleteCategory(Long id) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không thể tìm danh mục có mã là " + id));
        if(!existingCategory.getProducts().isEmpty()){
            throw new AuthException("Không thể xóa danh mục có sản phẩm!");
        }
        List<Category> subCategories = existingCategory.getCategories();
        for (Category subCategory : subCategories) {
            categoryRepository.delete(subCategory);
        }
        categoryRepository.delete(existingCategory);
    }

    @Override
    @Cacheable(key = "'all_page'")
    @Transactional(readOnly = true)
    public Page<CategoryResponse> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .map(CategoryResponse::fromEntity);
    }

    @Cacheable(key = "'all_list'"  )
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(CategoryResponse::fromEntity)
                .toList();
    }
}
