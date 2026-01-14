package com.example.FurnitureShop.Service.Interface;

import com.example.FurnitureShop.DTO.Request.ProductVariantRequest;
import com.example.FurnitureShop.DTO.Request.UpdateVariantRequest;
import com.example.FurnitureShop.DTO.Response.ProductVariantResponse;

public interface IProductVariant {

    ProductVariantResponse updateProductVariant(Long variantId, UpdateVariantRequest updateVariantRequest);


}
