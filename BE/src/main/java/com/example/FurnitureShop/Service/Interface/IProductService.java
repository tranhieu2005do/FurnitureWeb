package com.example.FurnitureShop.Service.Interface;

import com.example.FurnitureShop.DTO.Request.ProductRequest;
import com.example.FurnitureShop.DTO.Response.PageResponse;
import com.example.FurnitureShop.DTO.Response.ProductResponse;
import com.example.FurnitureShop.DTO.Response.ProductVariantResponse;
import com.example.FurnitureShop.Model.ProductVariant;
import com.example.FurnitureShop.Model.ProductVariant.Material;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface IProductService {

   ProductResponse createProduct(ProductRequest productRequest);

   Page<ProductResponse> getAllProducts(Pageable pageable);

   ProductResponse updateProduct(Long productId, ProductRequest productRequest);

   void deleteProduct(Long productId);

   List<ProductVariantResponse> getAllProductVariants(Long productId);

   void inActiveProduct(Long productId);

   List<ProductResponse> getProductsByCategoryId(Long categoryId);

   PageResponse<ProductResponse> getProductsByRangePrice(Pageable pageable, BigDecimal minPrice, BigDecimal maxPrice);

   List<ProductResponse> getProductsByMaterial(Material material);
}
