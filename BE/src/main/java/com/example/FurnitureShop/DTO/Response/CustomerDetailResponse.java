package com.example.FurnitureShop.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerDetailResponse {

    private String name;
    private LocalDate dateOfBirth;
    private String address;
    private String phone;
    private String email;
    private PageResponse<OrderResponse> totalOrders;
}
