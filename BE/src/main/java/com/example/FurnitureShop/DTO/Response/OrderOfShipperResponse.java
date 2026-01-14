package com.example.FurnitureShop.DTO.Response;

import com.example.FurnitureShop.Model.Order;
import com.example.FurnitureShop.Model.Order.InstallmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderOfShipperResponse {

    private String customerName;
    private String phone;
    private String trackingNumber;
    private InstallmentStatus installmentStatus;

    public static OrderOfShipperResponse fromEntity(Order order){
        return OrderOfShipperResponse.builder()
                .customerName(order.getName())
                .phone(order.getPhone())
                .trackingNumber(order.getTrackingNumber())
                .installmentStatus(order.getInstallmentStatus())
                .build();
    }
}
