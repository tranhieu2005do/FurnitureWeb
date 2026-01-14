package com.example.FurnitureShop.DTO.Response;

import com.example.FurnitureShop.Model.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    @JsonProperty("full_name")
    private String fullName;
    private String email;
    private String address;
    private String phone;

    @JsonProperty("date_of_birth")
    private LocalDate dateOfBirth;

    @JsonProperty("is_active")
    private Boolean isActive;
    private String role;

    public static UserResponse fromEntity(User user){
        return UserResponse.builder()
                .address(user.getAddress())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .dateOfBirth(user.getDateOfBirth())
                .isActive(user.isActive())
                .role(user.getRole().getName())
                .build();
    }
}
