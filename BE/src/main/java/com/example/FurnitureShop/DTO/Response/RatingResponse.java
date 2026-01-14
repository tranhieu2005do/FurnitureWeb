package com.example.FurnitureShop.DTO.Response;

import com.example.FurnitureShop.Model.Rating;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RatingResponse {
    @JsonProperty("customer_name")
    private String customerName;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("comments")
    private String comments;

    @JsonProperty("rate")
    private Integer rate;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    public static RatingResponse fromEntity(Rating rating) {
        return RatingResponse.builder()
                .customerName(rating.getUser().getFullName())
                .productName(rating.getProduct().getName())
                .comments(rating.getComments())
                .rate(rating.getStar())
                .createdAt(rating.getCreatedAt())
                .build();
    }
}
