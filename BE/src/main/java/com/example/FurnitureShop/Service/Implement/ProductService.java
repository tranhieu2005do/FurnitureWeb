package com.example.FurnitureShop.Service.Implement;

import com.example.FurnitureShop.DTO.Request.ProductRequest;
import com.example.FurnitureShop.DTO.Request.ProductVariantRequest;
import com.example.FurnitureShop.DTO.Request.UpdateVariantRequest;
import com.example.FurnitureShop.DTO.Response.PageResponse;
import com.example.FurnitureShop.DTO.Response.ProductResponse;
import com.example.FurnitureShop.DTO.Response.ProductVariantResponse;
import com.example.FurnitureShop.Exception.AuthException;
import com.example.FurnitureShop.Exception.NotFoundException;
import com.example.FurnitureShop.Model.Category;
import com.example.FurnitureShop.Model.Product;
import com.example.FurnitureShop.Model.ProductVariant;
import com.example.FurnitureShop.Model.ProductVariant.Material;
import com.example.FurnitureShop.Repository.CategoryRepository;
import com.example.FurnitureShop.Repository.ProductRepository;
import com.example.FurnitureShop.Repository.ProductVariantRepository;
import com.example.FurnitureShop.Service.Interface.IProductService;
import jakarta.persistence.Column;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
@Transactional
@CacheConfig(cacheNames = "products")
public class ProductService implements IProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductVariantService productVariantService;

    @Override
    @CacheEvict(allEntries = true)
    public ProductResponse createProduct(ProductRequest productRequest) {
        Category category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy danh mục"));
        if(!category.isActive()){
            throw new AuthException("Danh mục này không còn hoạt động!");
        }
        Product newProduct = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .isActive(true)
                .category(category)
                .createdAt(LocalDateTime.now())
                .purchaseCount(0)
                .updatedAt(LocalDateTime.now())
                .build();
        productRepository.save(newProduct);

        List<ProductVariant> newVariants = new ArrayList<>();
        Long inStock = 0L;
        for(ProductVariantRequest variantRequest : productRequest.getVariants()){
            ProductVariant newVariant = ProductVariant.builder()
                    .product(newProduct)
                    .color(variantRequest.getColor())
                    .material(variantRequest.getMaterial())
                    .price(variantRequest.getPrice())
                    .height(variantRequest.getHeight())
                    .length(variantRequest.getLength())
                    .width(variantRequest.getWidth())
                    .inStock(variantRequest.getInStock())
                    .build();

            inStock += variantRequest.getInStock();
            if(newVariant.getInStock() > 0){
                newVariant.setActive(true);
            }
            else{
                newVariant.setActive(false);
            }
            productVariantRepository.save(newVariant);
            newVariants.add(newVariant);
        }
        newProduct.setProductVariants(newVariants);
        newProduct.setInStockCount(inStock);
        productRepository.save(newProduct);
        return ProductResponse.fromEntity(newProduct);
    }

    @Override
    @Cacheable(key = "T(java.util.Objects).hash(#pageable.pageNumber, #pageable.pageSize)")
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        Page<ProductResponse> responsePage = products.map(ProductResponse::fromEntity);
        return responsePage;
    }

    @Override
    @CacheEvict(allEntries = true)
    public ProductResponse updateProduct(Long productId, ProductRequest productRequest) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy sản phẩm"));
        if(productRequest.getName() != null && !productRequest.getName().equals(existingProduct.getName())){
            if(productRepository.existsByName(productRequest.getName())){
                throw new AuthException("Đã tồn tại sản phẩm có tên " + productRequest.getName());
            }
            existingProduct.setName(productRequest.getName());
        }
        if(productRequest.getDescription() != null && !productRequest.getDescription().equals(existingProduct.getDescription())){
            existingProduct.setDescription(productRequest.getDescription());
        }
        List<ProductVariant> variants = existingProduct.getProductVariants();
        List<ProductVariant> newVariants = new ArrayList<>();
        if(productRequest.getVariants() != null){
            for(ProductVariantRequest variantRequest : productRequest.getVariants()){
                // Tạo updateVariantRequest
                UpdateVariantRequest updateVariantRequest = UpdateVariantRequest.builder()
                        .material(variantRequest.getMaterial())
                        .inStock(variantRequest.getInStock())
                        .price(variantRequest.getPrice())
                        .color(variantRequest.getColor())
                        .build();

                // Tạo updateVariant để đối chiếu trong danh sách variant của Product
                ProductVariant updateVariant = ProductVariant.builder()
                        .length(variantRequest.getLength())
                        .width(variantRequest.getWidth())
                        .height(variantRequest.getHeight())
                        .build();

                for(ProductVariant variant: variants){
                    if(updateVariant.sameSize(variant)
                            && updateVariant.getMaterial().equals(variant.getMaterial())
                            && updateVariant.getColor().equals(variant.getColor())
                    ){
                        productVariantService.updateProductVariant(variant.getId(), updateVariantRequest);
                        break;
                    }
                }
            }
        }
        existingProduct.setUpdatedAt(LocalDateTime.now());
        productRepository.save(existingProduct);
        return ProductResponse.fromEntity(existingProduct);
    }

    @Override
    @CacheEvict(allEntries = true)
    public void deleteProduct(Long productId) {
        Product existingProduct = productRepository.findById(productId).get();
        List<ProductVariant> variants = existingProduct.getProductVariants();
        for(ProductVariant variant: variants){
            productVariantRepository.deleteById(variant.getId());
        }
        productRepository.deleteById(productId);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(key = "'variant_' + #productId")
    public List<ProductVariantResponse> getAllProductVariants(Long productId) {
        Product existingProduct = productRepository.findById(productId).get();
        return existingProduct.getProductVariants()
                .stream()
                .map(ProductVariantResponse::fromEntity)
                .toList();
    }

    @Override
    @CacheEvict(allEntries = true)
    public void inActiveProduct(Long productId) {
        Product  existingProduct = productRepository.findById(productId).get();
        List<ProductVariant> variants = existingProduct.getProductVariants();
        for(ProductVariant variant: variants){
            variant.setActive(false);
            productVariantRepository.save(variant);
        }
        existingProduct.setUpdatedAt(LocalDateTime.now());
        existingProduct.setActive(false);
        productRepository.save(existingProduct);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(key = "'category_' + #categoryId")
    public List<ProductResponse> getProductsByCategoryId(Long categoryId) {
        Category existingCategory = categoryRepository.findById(categoryId).get();
        return existingCategory.getProducts()
                .stream()
                .map(ProductResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            key = "T(java.util.Objects).hash(#pageable.pageNumber, #pageable.pageSize, #page.sort, #minPrice, #maxPrice)"
    )
    public PageResponse<ProductResponse> getProductsByRangePrice(Pageable pageable, BigDecimal minPrice, BigDecimal maxPrice) {
        return PageResponse.fromPage(
                productRepository.getByRangePrice(pageable, minPrice, maxPrice)
                        .map(ProductResponse::fromEntity)
        );
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(key = "'material' + #material")
    public List<ProductResponse> getProductsByMaterial(Material material) {
        List<Product> products = productRepository.findAll();
        List<ProductResponse> responseList = new ArrayList<>();
        for(Product product: products){
            List<ProductVariant> variants = product.getProductVariants();
            boolean check = false;
            for(ProductVariant variant: variants){
                if(variant.getMaterial().equals(material)){
                    check = true;
                    break;
                }
            }
            if(check){
                responseList.add(ProductResponse.fromEntity(product));
            }
        }
        return responseList;
    }

    @CacheEvict(allEntries = true)
    public void deleteProductVariant(Long productId, Long variantId) {
        Product product = productRepository.findById(productId).get();
        ProductVariant variant = productVariantRepository.findById(variantId).get();
        product.setInStockCount(product.getInStockCount() - variant.getInStock());
        productRepository.save(product);
        productVariantRepository.deleteById(variantId);
    }

}
