package com.example.FurnitureShop.DTO.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RatingRequest {
    @JsonProperty("customer_id")
    @NotBlank(message = "Thiếu id của người dùng.")
    private Long customerId;

    @JsonProperty("product_id")
    @NotBlank(message = "Thiếu mã sản phẩm.")
    private Long productId;

    @JsonProperty("rate")
    @NotBlank(message = "Hãy đánh giá sản phẩm từ trên thang 5.")
    private Integer rate;

    @JsonProperty("comments")
    private String comments;
}
