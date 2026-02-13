package com.example.FurnitureShop.DTO.Response;

public record ProductRatingAvgResponse(
        Long productId,
        Double avgStar
) {

    public ProductRatingAvgResponse(Long productId, Double avgStar) {
        this.productId = productId;
        this.avgStar = avgStar;
    }
}
