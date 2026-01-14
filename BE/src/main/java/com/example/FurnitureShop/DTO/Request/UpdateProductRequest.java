package com.example.FurnitureShop.DTO.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class UpdateProductRequest {

    private String name;
    private String description;
}
