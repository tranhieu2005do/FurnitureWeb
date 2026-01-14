package com.example.FurnitureShop.DTO.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ProductImageRequest
{
    private Long variantId;
    private String url;
}
