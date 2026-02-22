package com.example.FurnitureShop.DTO.Request;

import com.example.FurnitureShop.Model.Order.PaymentStatus;
import com.example.FurnitureShop.Model.Order.OrderStatus;
import com.example.FurnitureShop.Model.Order.InstallmentStatus;
import com.example.FurnitureShop.Model.Order.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class OrderRequest {

    @JsonProperty("promotion_code")
    private List<String> promotionCodes;

//    @NotBlank(message = "Hãy chọn phương thức thanh toán cho đơn hàng của bạn.")
    @JsonProperty("payment_method")
    private PaymentMethod paymentMethod;

    @JsonProperty("note")
    private String note;

    @JsonProperty("item_list")
    private List<CartItemRequest> itemRequest;
}
