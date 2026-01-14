package com.example.FurnitureShop.DTO.Response;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
public class AuthResponse {
    private String phone;
    private String token;
}
