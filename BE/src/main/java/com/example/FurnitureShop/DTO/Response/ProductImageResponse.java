package com.example.FurnitureShop.DTO.Response;

import com.example.FurnitureShop.Model.ProductImage;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductImageResponse {

    private Long variantId;
    private String url;

    public static ProductImageResponse of(ProductImage productImage){
        return ProductImageResponse.builder()
                .url(productImage.getUrl())
                .variantId(productImage.getProductVariant().getId())
                .build();
    }
}
