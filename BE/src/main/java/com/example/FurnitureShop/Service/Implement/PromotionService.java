package com.example.FurnitureShop.Service.Implement;

import com.example.FurnitureShop.DTO.Request.PromotionRequest;
import com.example.FurnitureShop.DTO.Response.PromotionResponse;
import com.example.FurnitureShop.Exception.AuthException;
import com.example.FurnitureShop.Exception.NotFoundException;
import com.example.FurnitureShop.Model.*;
import com.example.FurnitureShop.Model.Promotion.DiscountType;
import com.example.FurnitureShop.Repository.ProductRepository;
import com.example.FurnitureShop.Repository.PromotionProductRepository;
import com.example.FurnitureShop.Repository.PromotionRepository;
import com.example.FurnitureShop.Repository.PromotionUserRepository;
import com.example.FurnitureShop.Service.Interface.IPromotionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
@CacheConfig(cacheNames = "promotions")
@Slf4j
public class PromotionService implements IPromotionService {

    private final PromotionRepository promotionRepository;
    private final PromotionProductRepository promotionProductRepository;
    private final PromotionUserRepository promotionUserRepository;
    private final PromotionUsageService promotionUsageService;
    private final ProductRepository productRepository;

    @Transactional
    @Override
    @CacheEvict(allEntries = true)
    public PromotionResponse create(PromotionRequest promotionRequest) {
        Promotion newPromotion = Promotion.builder()
                .code(promotionRequest.getCode())
                .startDate(promotionRequest.getStartDate())
                .endDate(promotionRequest.getEndDate())
                .discountType(promotionRequest.getDiscountType())
                .discountValue(promotionRequest.getDiscountValue())
                .isPersonal(promotionRequest.getIsPersonal())
                .build();
        promotionRepository.save(newPromotion);

        if(newPromotion.getDiscountType().equals(DiscountType.PRODUCT)){
            Product product = productRepository.findById(promotionRequest.getProductId())
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy sản phẩm"));

            if(!product.isActive()){
                throw new AuthException("Sản phẩm này hiện không còn được bán.");
            }
            PromotionProducts pProduct = PromotionProducts.builder()
                    .product(product)
                    .promotion(newPromotion)
                    .build();
            promotionProductRepository.save(pProduct);
        }
        return PromotionResponse.fromEntity(newPromotion);
    }

    @Transactional(readOnly = true)
    @Override
    @Cacheable(key = "'product_' + #productId + '_active_' + #active + '_time_' + #now")
    public List<PromotionResponse> filterByProduct(Long productId, boolean active) {
        return promotionRepository.filterByProduct(productId, active, LocalDateTime.now())
                .stream()
                .map(PromotionResponse::fromEntity)
                .toList();
    }

//    @Transactional
//    @Override
//    @CacheEvict(allEntries = true)
//    public void inActivePromotion(Long promotionId) {
//        Promotion promotion = promotionRepository.findById(promotionId)
//                .orElseThrow(() -> new RuntimeException("Khong tim thay phieu giam gia tuong ung"));
//        promotion.setActive(false);
//        promotionRepository.save(promotion);
//    }

    @Transactional(readOnly = true)
    @Override
    @Cacheable(key = "'code_' + #code")
    public PromotionResponse findByCode(String code) {
        return PromotionResponse.fromEntity(promotionRepository.findByCode(code));
    }

    @Transactional
    @CacheEvict(allEntries = true, condition = "#user != null")
    public void createBirthDayPromotionForUser(User user) {
        if( promotionRepository.existsPromotion(user.getId(), LocalDateTime.now().getYear())){
            log.info("Khách hàng đã có phiếu giảm giá sinh nhật trong năm nay.");
        }
        else{
            log.info("Creating promotion for user {}", user.getFullName());
            Promotion p = new Promotion();
            p.setCode("BDAY-" + user.getId() + "-" + UUID.randomUUID());
            p.setDiscountValue(25);
            p.setDiscountType(DiscountType.BIRTHDAY);
            p.setStartDate(LocalDateTime.now());
            p.setEndDate(LocalDateTime.now().plusDays(7));
            p.setIsPersonal(true);
            promotionRepository.save(p);

            PromotionUsers pUser = PromotionUsers.builder()
                    .user(user)
                    .promotion(p)
                    .build();
            promotionUserRepository.save(pUser);
        }
    }

    public Pair<BigDecimal,BigDecimal> applyPromotion(Promotion promotion, BigDecimal totalPrice, List<CartItem> cartItems, Long userId) {
        BigDecimal valDiscount = BigDecimal.ZERO;
        if(promotion.getDiscountType().equals(DiscountType.BIRTHDAY)){
            PromotionUsers pUser = promotionUserRepository.findByPromotionId(promotion.getId());
            if(pUser != null && pUser.getUsedAt() == null){
                BigDecimal discount = BigDecimal.valueOf(promotion.getDiscountValue());
                valDiscount = totalPrice.multiply(discount)
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                totalPrice = totalPrice.subtract(valDiscount);
                pUser.setUsedAt(LocalDateTime.now());
                promotionUserRepository.save(pUser);
            }
        }
        else if(promotion.getDiscountType().equals(DiscountType.SHIPPING)){

        }
        else if(promotion.getDiscountType().equals(DiscountType.ORDER)){
            BigDecimal discount = BigDecimal.valueOf( promotion.getDiscountValue());
            valDiscount = totalPrice.multiply(discount)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            totalPrice = totalPrice.subtract(valDiscount);
            promotionUsageService.applyPromotion(promotion, userId);
        }
        else{
            PromotionProducts pProduct = promotionProductRepository.findByPromotionId(promotion.getId());
            for(CartItem cartItem : cartItems){
                if(cartItem.getProductVariant().getProduct().getId().equals(pProduct.getProduct().getId())){
                    valDiscount = cartItem.getProductVariant().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()))
                            .multiply(BigDecimal.valueOf(promotion.getDiscountValue()))
                            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                    totalPrice = totalPrice.subtract(valDiscount);
                    promotionUsageService.applyPromotion(promotion, userId);
                    break;
                }
            }
        }
        Pair<BigDecimal,BigDecimal> result = Pair.of(totalPrice,valDiscount);
        return result;
    }
}
