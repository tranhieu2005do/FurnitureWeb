package com.example.FurnitureShop.DTO.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {
    @NotBlank(message = "Bạn phải nhập số điện thoại.")
    private String phone;

    @NotBlank(message = "Mật khẩu không thể bỏ trống.")
    private String password;
}
