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

    @NotBlank(message = "Thiếu tên khách hàng.")
    @JsonProperty("name")
    private String name;

    @JsonProperty("user_id")
    @NotNull(message = "Thiếu mã định danh người dùng.")
    private Long userId;

    @JsonProperty("staff_id")
    private Long staffId;

    @JsonProperty("promotion_code")
    private String promotionCode;

//    @NotBlank(message = "Hãy chọn phương thức thanh toán cho đơn hàng của bạn.")
    @JsonProperty("payment_method")
    private PaymentMethod paymentMethod;

    @JsonProperty("address")
    @NotBlank(message = "Thiếu địa chỉ để giao hàng.")
    private String address;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("note")
    private String note;
}
