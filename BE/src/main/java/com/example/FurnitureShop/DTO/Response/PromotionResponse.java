package com.example.FurnitureShop.DTO.Response;

import com.example.FurnitureShop.Model.Promotion;
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
public class PromotionResponse {

    @JsonProperty("code")
    private String code;

    @JsonProperty("discount_value")
    private Integer discountValue;

    @JsonProperty("end_date")
    private LocalDateTime endDate;

    @JsonProperty("start_date")
    private LocalDateTime startDate;

    @JsonProperty("is_personal")
    private Boolean isPersonal;

    public static PromotionResponse fromEntity(Promotion promotion) {
        return PromotionResponse.builder()
                .code(promotion.getCode())
                .discountValue(promotion.getDiscountValue())
                .endDate(promotion.getEndDate())
                .startDate(promotion.getStartDate())
                .isPersonal(promotion.getIsPersonal())
                .build();
    }
}
