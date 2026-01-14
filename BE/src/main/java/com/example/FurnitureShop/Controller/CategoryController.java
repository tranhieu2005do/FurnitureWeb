package com.example.FurnitureShop.Controller;

import com.example.FurnitureShop.DTO.Request.CategoryRequest;
import com.example.FurnitureShop.DTO.Response.ApiResponse;
import com.example.FurnitureShop.DTO.Response.CategoryResponse;
import com.example.FurnitureShop.Model.Category;
import com.example.FurnitureShop.Service.Implement.CategoryService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/category")
public class CategoryController {

    private final CategoryService categoryService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@RequestBody @Valid CategoryRequest categoryRequest) {
        CategoryResponse category = categoryService.createCategory(categoryRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<CategoryResponse>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message("Tạo danh mục thành công")
                        .data(category)
                        .build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        List<CategoryResponse> response = categoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.<List<CategoryResponse>>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .data(response)
                        .message("Lấy thành công danh sách danh mục sản phẩm")
                        .build());
    }

    @GetMapping("/root")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getRootCategories() {
        List<CategoryResponse> response = categoryService.getRootCategories();
        return ResponseEntity.ok(ApiResponse.<List<CategoryResponse>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(response)
                        .message("Lấy thành công danh sách danh mục gốc")
                        .build());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(@PathVariable Long id, @RequestBody @Valid CategoryRequest categoryRequest) {
        CategoryResponse category = categoryService.updateCategory(id, categoryRequest);
        return ResponseEntity.ok(ApiResponse.<CategoryResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Cập nhật danh mục thành công")
                        .data(category)
                        .build());
    }

    @GetMapping("/{rootCategoryId}/sub")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getSubCategories(@PathVariable Long rootCategoryId) {
        List<CategoryResponse> response = categoryService.getSubCategories(rootCategoryId);
        return ResponseEntity.ok(ApiResponse.<List<CategoryResponse>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Lấy danh sách danh mục con thành công")
                        .data(response)
                        .build());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id){
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Xóa danh mục thành công")
                        .build());
    }
}
