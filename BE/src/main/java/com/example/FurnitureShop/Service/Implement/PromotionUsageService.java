package com.example.FurnitureShop.Service.Implement;

import com.example.FurnitureShop.Exception.AuthException;
import com.example.FurnitureShop.Model.Promotion;
import com.example.FurnitureShop.Model.PromotionUsage;
import com.example.FurnitureShop.Model.User;
import com.example.FurnitureShop.Repository.PromotionRepository;
import com.example.FurnitureShop.Repository.PromotionUsageRepository;
import com.example.FurnitureShop.Repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Transactional
@CacheConfig(cacheNames = "promotionUsages")
public class PromotionUsageService {
    private final PromotionUsageRepository promotionUsageRepository;
    private final PromotionRepository promotionRepository;
    private final UserRepository userRepository;

    @Transactional
    @CacheEvict(allEntries = true)
    public void applyPromotion(Promotion promotion, Long userId){
        try{
            User user = userRepository.findById(userId).get();
            promotionUsageRepository.save(PromotionUsage.builder()
                    .promotion(promotion)
                    .user(user)
                    .used(LocalDateTime.now())
                    .build());
        }
        catch (DataIntegrityViolationException ex){
            throw new AuthException("User đã sử dụng khuyến mãi này");
        }
    }
}
