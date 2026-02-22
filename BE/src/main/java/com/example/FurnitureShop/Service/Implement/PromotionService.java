package com.example.FurnitureShop.Service.Implement;

import com.example.FurnitureShop.DTO.Request.PromotionRequest;
import com.example.FurnitureShop.DTO.Response.ApplyPromotionResponse;
import com.example.FurnitureShop.DTO.Response.PromotionResponse;
import com.example.FurnitureShop.DTO.Response.PromotionUserCanUse;
import com.example.FurnitureShop.Exception.AuthException;
import com.example.FurnitureShop.Exception.NotFoundException;
import com.example.FurnitureShop.Model.*;
import com.example.FurnitureShop.Model.Promotion.DiscountType;
import com.example.FurnitureShop.Repository.*;
import com.example.FurnitureShop.Service.Interface.IPromotionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
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
    private final UserRepository  userRepository;
    private final PromotionUsageRepository promotionUsageRepository;

    @Transactional
    @Override
//    @CacheEvict(allEntries = true)
    public PromotionResponse createProductPromotion(PromotionRequest promotionRequest) {
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

    private String generateCode() {
        return "BD-" + UUID.randomUUID().toString().substring(0,8).toUpperCase();
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Ho_Chi_Minh")
    public void createUserPromotion() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        LocalDate targetDate = today.plusDays(3);

        int month = targetDate.getMonthValue();
        int day = targetDate.getDayOfMonth();

        List<User> users = userRepository.findByMonthAndDay(month, day);

        for(User u : users){
            Promotion newPromotion = Promotion.builder()
                    .code("BD-" + u.getDateOfBirth().toString() + u.getId() + LocalDateTime.now().getYear())
                    .startDate(LocalDateTime.now())
                    .endDate(LocalDateTime.now().plusDays(7))
                    .discountType(DiscountType.BIRTHDAY)
                    .isPersonal(true)
                    .discountValue(20)
                    .build();

            promotionRepository.save(newPromotion);

            PromotionUsers newPromotionUser =  PromotionUsers.builder()
                    .promotion(newPromotion)
                    .user(u)
                    .build();

            promotionUserRepository.save(newPromotionUser);
        }
    }

    @Transactional
//    @Cacheable(key = "'user_' + #userId")
    public List<PromotionUserCanUse> activeByUser(Long userId){

        List<PromotionUserCanUse> responses = new ArrayList<>();

        // Load promotionUser
        List<PromotionUsers> pUsers = promotionUserRepository.findPromotionActiveByUserId(userId);
        for(PromotionUsers pUser : pUsers){
            PromotionUserCanUse p = PromotionUserCanUse.builder()
                    .code(pUser.getPromotion().getCode())
                    .promotionId(pUser.getPromotion().getId())
                    .userId(userId)
                    .type(DiscountType.BIRTHDAY)
                    .discountValue(pUser.getPromotion().getDiscountValue())
                    .startDate(pUser.getPromotion().getStartDate())
                    .endDate(pUser.getPromotion().getEndDate())
                    .productId(null)
                    .productName(null)
                    .build();
            responses.add(p);
        }

        // Load active promotion except birthday promotion
        List<Promotion> promotions = promotionRepository.activePromotionExceptType(userId, DiscountType.BIRTHDAY);
        for(Promotion p : promotions){
            Long productId = null;
            String productName = null;
            if(p.getDiscountType().equals(DiscountType.PRODUCT)){
                PromotionProducts pProduct = promotionProductRepository.findByPromotionId(p.getId());
                productId = pProduct.getProduct().getId();
                productName = pProduct.getProduct().getName();
            }
            PromotionUserCanUse pUC = PromotionUserCanUse.builder()
                    .code(p.getCode())
                    .productName(productName)
                    .promotionId(p.getId())
                    .userId(userId)
                    .type(p.getDiscountType())
                    .discountValue(p.getDiscountValue())
                    .startDate(p.getStartDate())
                    .endDate(p.getEndDate())
                    .productId(productId)
                    .build();
            responses.add(pUC);
        }
        return responses;
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
    public ApplyPromotionResponse applyPromotion(
            Promotion promotion,
            List<CartItem> cartItems,
            BigDecimal subTotalPrice,
            Long orderId,
            User user
    ){
        ApplyPromotionResponse response = ApplyPromotionResponse.builder()
                .code(promotion.getCode())
                .promotionId(promotion.getId())
                .discountType(promotion.getDiscountType())
                .build();
        if(promotion.getDiscountType().equals(DiscountType.BIRTHDAY)){
            PromotionUsers pU = promotionUserRepository.findByPromotionIdAndUserId(promotion.getId(), user.getId());
            BigDecimal discountValue = subTotalPrice
                    .multiply(BigDecimal.valueOf(promotion.getDiscountValue()))
                    .divide(BigDecimal.valueOf(100));
            response.setDiscountValue(discountValue);
            response.setProductName(null);
            pU.setUsedAt(LocalDateTime.now());
            pU.setOrderId(orderId);
            promotionUserRepository.save(pU);
        }
        else if (promotion.getDiscountType().equals(DiscountType.PRODUCT)){
            PromotionProducts pProduct = promotionProductRepository.findByPromotionId(promotion.getId());
            response.setProductName(pProduct.getProduct().getName());
            BigDecimal totalDiscount = BigDecimal.ZERO;
            for(CartItem cartItem : cartItems){
                if(cartItem.getProductVariant().getProduct().getId().equals(pProduct.getProduct().getId())){
                    BigDecimal discountValue = cartItem.getProductVariant().getPrice()
                            .multiply(BigDecimal.valueOf(cartItem.getQuantity()))
                            .multiply(BigDecimal.valueOf(promotion.getDiscountValue()))
                            .divide(BigDecimal.valueOf(100));
                    totalDiscount = totalDiscount.add(discountValue);
                    PromotionUsage pU = PromotionUsage.builder()
                            .used(LocalDateTime.now())
                            .user(user)
                            .promotion(promotion)
                            .orderId(orderId)
                            .build();
                    promotionUsageRepository.save(pU);
                }
            }
            response.setDiscountValue(totalDiscount);
        }
        return response;
    }
}
