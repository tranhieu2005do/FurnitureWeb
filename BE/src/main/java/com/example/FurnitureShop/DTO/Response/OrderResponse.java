package com.example.FurnitureShop.DTO.Response;

import com.example.FurnitureShop.Model.Order;
import com.example.FurnitureShop.Model.Order.PaymentStatus;
import com.example.FurnitureShop.Model.Order.OrderStatus;
import com.example.FurnitureShop.Model.Order.PaymentMethod;
import com.example.FurnitureShop.Model.Order.InstallmentStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class OrderResponse {

    @JsonProperty("name")
    private String name;

    @JsonProperty("staff_name")
    private String staffName;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("address")
    private String address;

    @JsonProperty("note")
    private String note;

    @JsonProperty("order_date")
    private LocalDateTime orderDate;

    @JsonProperty("installment_status")
    private InstallmentStatus installmentStatus;

    @JsonProperty("order_status")
    private OrderStatus orderStatus;

    @JsonProperty("payment_method")
    private PaymentMethod paymentMethod;

    @JsonProperty("tracking_number")
    private String trackingNumber;

    @JsonProperty("payment_status")
    private PaymentStatus paymentStatus;

    @JsonProperty("total_price")
    private BigDecimal totalPrice;

    public  static OrderResponse fromEntity(Order order) {
        return OrderResponse.builder()
                .name(order.getName())
                .staffName(order.getStaff().getFullName())
                .phone(order.getPhone())
                .address(order.getAddress())
                .note(order.getNote())
                .orderDate(order.getOrderDate())
                .installmentStatus(order.getInstallmentStatus())
                .orderStatus(order.getOrderStatus())
                .paymentMethod(order.getPaymentMethod())
                .trackingNumber(order.getTrackingNumber())
                .paymentStatus(order.getPaymentStatus())
                .totalPrice(order.getTotalPrice())
                .build();
    }
}
