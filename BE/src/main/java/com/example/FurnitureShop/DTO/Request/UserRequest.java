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
    @JsonProperty("full_name")
    private String fullName;

    @NotBlank(message = "Vui lòng nhập địa chỉ email của bạn.")
    private String email;

    @NotBlank(message = "Vui lòng nhập số điện thoại của bạn.")
    @Size(min = 10, message = "Số điện thoại phải đủ 10 số.")
    private String phone;

    @Size(min = 8, message = "Mật khẩu phải tối thiểu 8 kí tự.")
    @NotBlank(message = "Bạn phải đặt mật khẩu")
    private String password;

    private String address;

    @JsonProperty("date_of_birth")
    private LocalDate birthday;


}
