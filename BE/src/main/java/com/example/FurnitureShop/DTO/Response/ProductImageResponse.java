package com.example.FurnitureShop.DTO.Response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductImageResponse {

    private Long variantId;
    private String url;
}
