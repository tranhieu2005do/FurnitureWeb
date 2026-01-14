package com.example.FurnitureShop.DTO.Response;

import com.example.FurnitureShop.Model.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder
public class AccountResponse {

    private String phone;

    private String password;

    @JsonProperty("full_name")
    private String fullName;

    private String address;

    @JsonProperty("date_of_birth")
    private LocalDate dateOfBirth;

    @JsonProperty("is_active")
    private boolean isActive;

    @JsonProperty("role_id")
    private int roleId;

    public static AccountResponse fromUser(User user){
        return AccountResponse.builder()
                .phone(user.getPhone())
                .password(user.getPassword())
                .fullName(user.getFullName())
                .dateOfBirth(user.getDateOfBirth())
                .isActive(user.isActive())
                .roleId(user.getRole().getId())
                .build();
    }
}
