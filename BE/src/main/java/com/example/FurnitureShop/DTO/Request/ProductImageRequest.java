package com.example.FurnitureShop.DTO.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class ProductImageRequest {

    @JsonProperty("image")
    private MultipartFile imageFile;

    @JsonProperty("variant_id")
    private Long variantId;
}
