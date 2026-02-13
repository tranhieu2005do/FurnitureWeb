package com.example.FurnitureShop.Service.Interface;

import com.example.FurnitureShop.DTO.Request.ProductRequest;
import com.example.FurnitureShop.DTO.Response.PageResponse;
import com.example.FurnitureShop.DTO.Response.ProductResponse;
import com.example.FurnitureShop.DTO.Response.ProductVariantResponse;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface IProductService {

   ProductResponse createProduct(ProductRequest productRequest);

   ProductResponse updateProduct(Long productId, ProductRequest productRequest);

   void deleteProduct(Long productId);

   List<ProductVariantResponse> getAllProductVariants(Long productId);

   void inActiveProduct(Long productId);

   PageResponse<ProductResponse> getProducts(Pageable pageable,
                                             BigDecimal minPrice,
                                             BigDecimal maxPrice,
                                             Long categoryId,
                                             Float star,
                                             Boolean inStock,
                                             String sortBy
                                             );

   PageResponse<ProductVariantResponse> getVariants(Pageable pageable,
                                                    String color,
                                                    BigDecimal length,
                                                    BigDecimal height,
                                                    BigDecimal width,
                                                    String material,
                                                    Boolean inStock);
}
