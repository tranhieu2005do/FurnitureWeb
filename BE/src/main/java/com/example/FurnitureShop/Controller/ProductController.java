package com.example.FurnitureShop.Controller;

import com.example.FurnitureShop.DTO.Request.ProductImageRequest;
import com.example.FurnitureShop.DTO.Request.ProductRequest;
import com.example.FurnitureShop.DTO.Request.ProductVariantRequest;
import com.example.FurnitureShop.DTO.Response.*;
import com.example.FurnitureShop.Service.Implement.CloudinaryService;
import com.example.FurnitureShop.Service.Implement.ProductService;
import com.example.FurnitureShop.Service.Implement.ProductVariantService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/product")
@AllArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductVariantService productVariantService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @RequestBody @Valid ProductRequest productRequest)
    {
        ProductResponse productResponse = productService.createProduct(productRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<ProductResponse>builder()
                        .data(productResponse)
                        .statusCode(HttpStatus.CREATED.value())
                        .message("Tạo sản phẩm thành công")
                        .build());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @PostMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductVariantResponse>> insertProductVariant(
            @PathVariable Long productId,
            @RequestBody @Valid ProductVariantRequest productVariantRequest)
    {
        ProductVariantResponse productVariantResponse = productVariantService.insertProductVariant(productId, productVariantRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<ProductVariantResponse>builder()
                .data(productVariantResponse)
                .statusCode(HttpStatus.CREATED.value())
                .message("Thêm thành công mẫu sản phẩm mới")
                .build());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @DeleteMapping("/{productId}/{variantId}")
    public ResponseEntity<ApiResponse<Void>> deleteVariant(
            @PathVariable Long productId,
            @PathVariable Long variantId)
    {
        productService.deleteProductVariant(productId, variantId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Xóa mẫu sản phẩm thành công")
                .build());
    }

    @GetMapping("/{productId}/variant")
    public ResponseEntity<ApiResponse<List<ProductVariantResponse>>> getProductVariants(
            @PathVariable Long productId)
    {
        List<ProductVariantResponse> responses = productService.getAllProductVariants(productId);
        return ResponseEntity.ok(ApiResponse.<List<ProductVariantResponse>>builder()
                .message("Lay thành công danh sách mẫu sản phẩm!!")
                .statusCode(HttpStatus.OK.value())
                .data(responses)
                .build());
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProduct(@PathVariable Long productId){
        return ResponseEntity.ok(ApiResponse.<ProductResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Get product successfully")
                .data(productService.getProductByProductId(productId))
                .build());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @PatchMapping("/{productId}/soft_delete")
    public ResponseEntity<ApiResponse<Void>> softDeleteProduct(
            @PathVariable Long productId)
    {
        productService.inActiveProduct(productId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Vô hiệu hóa sản phẩm thành công!")
                        .build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @PathVariable Long productId)
    {
        productService.deleteProduct(productId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Xóa sản phẩm thành công!!")
                .build());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long productId,
            @RequestBody @Valid ProductRequest productRequest)
    {
        ProductResponse response = productService.updateProduct(productId, productRequest);
        return ResponseEntity.ok(ApiResponse.<ProductResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Cập nhật sản phẩm thành công!")
                .data(response)
                .build());
    }

    @PostMapping("/variant/image")
    public ResponseEntity<ApiResponse<ProductImageResponse>> uploadVariantImage(@RequestBody ProductImageRequest request) throws IOException {
        String folder = "furniture-web/products";
        return ResponseEntity.ok(ApiResponse.<ProductImageResponse>builder()
                .data(productService.uploadVariantImage(request))
                .message("Upload variant image successfully")
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getProducts(
            Pageable pageable,
            @RequestParam(name = "min_price", required = false) BigDecimal minPrice,
            @RequestParam(name = "max_price", required = false) BigDecimal maxPrice,
            @RequestParam(name = "category_id", required = false) Long categoryId,
            @RequestParam(name = "star", required = false) Float star,
            @RequestParam(name = "in_stock", required = false) Boolean inStock,
            @RequestParam(name = "sort_by", required = false) String sortBy
    ){
        return ResponseEntity.ok(ApiResponse.<PageResponse<ProductResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Get successfully")
                .data(productService.getProducts(pageable, minPrice, maxPrice, categoryId, star, inStock, sortBy))
                .build());
    }

    @GetMapping("/variant")
    public ResponseEntity<ApiResponse<PageResponse<ProductVariantResponse>>> getVariants(
            Pageable pageable,
            @RequestParam(name = "color", required = false) String color,
            @RequestParam(name = "length", required = false) BigDecimal length,
            @RequestParam(name = "height", required = false) BigDecimal height,
            @RequestParam(name = "width", required = false) BigDecimal width,
            @RequestParam(name = "material", required = false) String material,
            @RequestParam(name = "in_stock", required = false) Boolean inStock
    ){
        return ResponseEntity.ok(ApiResponse.<PageResponse<ProductVariantResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .data(productService.getVariants(pageable, color, length, height, width, material, inStock))
                .message("Get successfully")
                .build());
    }



}
