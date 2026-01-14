package com.example.FurnitureShop.DTO.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ChangePasswordRequest {
    @NotBlank(message = "Hãy nhập mật khẩu cũ để xác thưc.")
    private String oldPassword;

    @NotBlank(message = "Nhập mật khẩu mới của bạn.")
    private String newPassword;
}
