package com.example.FurnitureShop.Service.Interface;

import com.example.FurnitureShop.DTO.Request.RatingRequest;
import com.example.FurnitureShop.DTO.Response.RatingResponse;

import java.util.List;

public interface RatingService {
    RatingResponse createrating(RatingRequest request);

    List<RatingResponse> getRateOfProduct(Long productId, Integer star, boolean comments);

    void deleteRating(Long ratingId);
}
