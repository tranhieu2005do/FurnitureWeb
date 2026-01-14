package com.example.FurnitureShop.DTO.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifyRequest {

    @NotBlank(message = "Không có token thì verify kiểu gì !!!!")
    private String token;
}
