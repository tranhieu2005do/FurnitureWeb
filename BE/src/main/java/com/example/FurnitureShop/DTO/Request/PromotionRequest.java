package com.example.FurnitureShop.DTO.Request;

import com.example.FurnitureShop.Model.Promotion.DiscountType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PromotionRequest {

    @NotBlank(message = "Phải có mã giảm giá.")
    @JsonProperty("code")
    private String code;

    @JsonProperty("discount_type")
    private DiscountType discountType;

    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("discount_value")
    @NotNull(message = "Hãy nhập giá trị giảm giá.")
    private Integer discountValue;

    @JsonProperty("start_date")
    @NotNull(message = "Thiếu thời gian bắt đầu hiệu lực giảm giá.")
    private LocalDateTime startDate;

    @JsonProperty("end_date")
    @NotNull(message = "Thiếu thời gian kết thục hiệu lực giảm giá.")
    private LocalDateTime endDate;

    @JsonProperty("is_personal")
    private Boolean isPersonal;
}
