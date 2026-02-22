package com.example.FurnitureShop.DTO.Response;

import com.example.FurnitureShop.Model.Promotion.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionUserCanUse {

    private String code;
    private String productName;
    private Long promotionId;
    private DiscountType type;
    private Integer discountValue;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long productId;
    private Long userId;

}
