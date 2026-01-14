package com.example.FurnitureShop.DTO.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserRequest {

    @JsonProperty("full_name")
    private String fullName;
    private String email;
    private String address;
    private String phone;

    @JsonProperty("date_of_birth")
    private LocalDate  dateOfBirth;
}
