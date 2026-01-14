package com.example.FurnitureShop.Controller;

import com.example.FurnitureShop.DTO.Request.ProductRequest;
import com.example.FurnitureShop.DTO.Request.ProductVariantRequest;
import com.example.FurnitureShop.DTO.Response.*;
import com.example.FurnitureShop.Model.Product;
import com.example.FurnitureShop.Model.ProductVariant.Material;
import com.example.FurnitureShop.Repository.ProductRepository;
import com.example.FurnitureShop.Repository.ProductVariantRepository;
import com.example.FurnitureShop.Service.Implement.ProductImageService;
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
    private final ProductImageService productImageService;

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

    @GetMapping("/{productId}")
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

    @GetMapping("/material")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getProductsByMaterial(
            @RequestParam Material material){
        List<ProductResponse> response = productService.getProductsByMaterial(material);
        return ResponseEntity.ok(ApiResponse.<List<ProductResponse>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Lấy thành công danh sách sản phẩm theo nguyên liệu sản phẩm")
                        .data(response)
                        .build());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @PostMapping("/variant/{variantId}")
    public ResponseEntity<ApiResponse<List<ProductImageResponse>>> uploadVariantImage(
            @PathVariable Long variantId,
            @RequestParam List<MultipartFile> files
    ) throws IOException {
        List<ProductImageResponse> response = productImageService.createProductImage(variantId, files);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<List<ProductImageResponse>>builder()
                        .data(response)
                        .statusCode(HttpStatus.CREATED.value())
                        .message("Cập nhật ảnh sản phẩm thành công!!")
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

    @GetMapping("/price")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getProductInRangePrice(
            Pageable pageable,
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice
            ){
        return ResponseEntity.ok(ApiResponse.<PageResponse<ProductResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Get successfully")
                .data(productService.getProductsByRangePrice(pageable, minPrice, maxPrice))
                .build());
    }

}
