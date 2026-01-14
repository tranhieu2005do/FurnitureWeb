package com.example.FurnitureShop.DTO.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class AccountResquest {

    @NotBlank(message = "Hãy nhập tên người dùng.")
    @JsonProperty("full_name")
    private String fullName;

    @NotBlank(message = "Nhập số điện thoại của bạn.")
    private String phone;

    @NotBlank(message = "Nhập mật khẩu của bạn.")
    private String password;

    private String address;

    @JsonProperty("role_id")
    @NotBlank(message = "Thiếu mã định danh vai trò.")
    private Integer roleId;

    @JsonProperty("date_of_birth")
    private LocalDate dateOfBirth;

    private String email;
}
