package com.example.FurnitureShop.Service.Implement;

import com.example.FurnitureShop.DTO.Request.RatingRequest;
import com.example.FurnitureShop.DTO.Response.RatingResponse;
import com.example.FurnitureShop.Exception.NotFoundException;
import com.example.FurnitureShop.Model.Product;
import com.example.FurnitureShop.Model.Rating;
import com.example.FurnitureShop.Model.User;
import com.example.FurnitureShop.Repository.ProductRepository;
import com.example.FurnitureShop.Repository.RatingRepository;
import com.example.FurnitureShop.Repository.UserRepository;
import com.example.FurnitureShop.Service.Interface.RatingService;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional
@CacheConfig(cacheNames = "ratings")
public class RateService implements RatingService {

    private final UserRepository userRepository;
    private final RatingRepository ratingRepository;
    private final ProductRepository productRepository;

    @Transactional
    @Override
    @CacheEvict(allEntries = true)
    public RatingResponse createrating(RatingRequest request) {
        User customer = userRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new NotFoundException("Customer Not Found"));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new NotFoundException("Product Not Found"));
        Rating newRating = Rating.builder()
                .user(customer)
                .product(product)
                .comments(request.getComments())
                .star(request.getRate())
                .createdAt(LocalDateTime.now())
                .build();
        ratingRepository.save(newRating);
        return RatingResponse.fromEntity(newRating);
    }

    @Transactional(readOnly = true)
    @Override
    @Cacheable(key = "'product_' + #productId + '_star_' + #star + _comments_ + #comments")
    public List<RatingResponse> getRateOfProduct(Long productId, Integer star, boolean comments) {
        return ratingRepository.filterByProduct(productId, star, comments)
                .stream()
                .map(RatingResponse::fromEntity)
                .toList();
    }

    @Override
    @CacheEvict(allEntries = true)
    public void deleteRating(Long ratingId) {
        Rating rate = ratingRepository.findById(ratingId)
                        .orElseThrow(() -> new NotFoundException("Rating Not Found"));
        ratingRepository.delete(rate);
    }


}
