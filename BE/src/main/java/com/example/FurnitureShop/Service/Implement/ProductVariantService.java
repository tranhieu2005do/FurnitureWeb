package com.example.FurnitureShop.Service.Implement;

import com.example.FurnitureShop.DTO.Request.ProductVariantRequest;
import com.example.FurnitureShop.DTO.Request.UpdateVariantRequest;
import com.example.FurnitureShop.DTO.Response.ProductVariantResponse;
import com.example.FurnitureShop.Exception.AuthException;
import com.example.FurnitureShop.Exception.NotFoundException;
import com.example.FurnitureShop.Model.Product;
import com.example.FurnitureShop.Model.ProductVariant;
import com.example.FurnitureShop.Repository.ProductRepository;
import com.example.FurnitureShop.Repository.ProductVariantRepository;
import com.example.FurnitureShop.Service.Interface.IProductVariant;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
@CacheConfig("product_variants")
public class ProductVariantService implements IProductVariant {
    private final ProductVariantRepository productVariantRepository;
    private final ProductRepository productRepository;

    @Override
    @CacheEvict(allEntries = true)
    public ProductVariantResponse updateProductVariant(Long variantId, UpdateVariantRequest updateVariantRequest) {
        ProductVariant variant = productVariantRepository.findById(variantId).get();
        variant.setPrice(updateVariantRequest.getPrice());
        variant.setColor(updateVariantRequest.getColor());
        variant.setMaterial(updateVariantRequest.getMaterial());
        variant.setInStock(updateVariantRequest.getInStock());
        productVariantRepository.save(variant);
        return ProductVariantResponse.fromEntity(variant);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public ProductVariantResponse insertProductVariant(Long productId, ProductVariantRequest productVariantRequest) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy sản phẩm có mã " + productId));
        ProductVariant newVariant = ProductVariant.builder()
                .product(existingProduct)
                .price(productVariantRequest.getPrice())
                .color(productVariantRequest.getColor())
                .material(productVariantRequest.getMaterial())
                .inStock(productVariantRequest.getInStock())
                .length(productVariantRequest.getLength())
                .height(productVariantRequest.getHeight())
                .width(productVariantRequest.getWidth())
                .build();
        List<ProductVariant> variantList = existingProduct.getProductVariants();
        for(ProductVariant productVariant : variantList){
            if(productVariant.sameSize(newVariant) && productVariant.getMaterial().equals(newVariant.getMaterial())
            && productVariant.getColor().equals(newVariant.getColor())){
                throw new AuthException("Mẫu sản phẩm này đã tồn tại!!");
            }
        }
        existingProduct.getProductVariants().add(newVariant);
        existingProduct.setInStockCount(productVariantRequest.getInStock() + newVariant.getInStock());
        productRepository.save(existingProduct);
        productVariantRepository.save(newVariant);
        return ProductVariantResponse.fromEntity(newVariant);
    }

}
