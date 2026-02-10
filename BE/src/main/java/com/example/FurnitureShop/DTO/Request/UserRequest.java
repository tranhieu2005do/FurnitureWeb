package com.example.FurnitureShop.DTO.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequest {

    @NotBlank(message = "Hãy cho tôi biết tên của bạn.")
    @JsonProperty("name")
    private String fullName;

    @NotBlank(message = "Vui lòng nhập địa chỉ email của bạn.")
    @JsonProperty("email")
    private String email;

    @NotBlank(message = "Vui lòng nhập số điện thoại của bạn.")
    @Size(min = 10, message = "Số điện thoại phải đủ 10 số.")
    @JsonProperty("phone")
    private String phone;

    @Size(min = 8, message = "Mật khẩu phải tối thiểu 8 kí tự.")
    @NotBlank(message = "Bạn phải đặt mật khẩu")
    @JsonProperty("password")
    private String password;

    @JsonProperty("address")
    private String address;

    @JsonProperty("birth")
    private LocalDate birthday;


}
