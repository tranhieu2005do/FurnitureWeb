package com.example.FurnitureShop.Service.Interface;

import com.example.FurnitureShop.DTO.Request.PromotionRequest;
import com.example.FurnitureShop.DTO.Response.PromotionResponse;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface IPromotionService {

    PromotionResponse createProductPromotion(PromotionRequest promotionRequest);

    List<PromotionResponse> filterByProduct(Long productId, boolean active);

//    void inActivePromotion(Long promotionId);

    PromotionResponse findByCode(String code);
}
