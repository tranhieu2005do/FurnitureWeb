package com.example.FurnitureShop.DTO.Response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UploadResult {
    @JsonProperty("url")
    private String url;

    @JsonProperty("public_id")
    private String publicId;

    @JsonProperty("type")
    private String resourceType;

    @JsonProperty("thumbnail")
    private String thumbnailUrl;

    @JsonProperty("duration")
    private Double duration;
}
