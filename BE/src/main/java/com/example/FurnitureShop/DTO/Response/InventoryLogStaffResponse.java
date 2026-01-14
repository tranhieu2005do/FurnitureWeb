package com.example.FurnitureShop.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryLogStaffResponse {

    private String name;
    private String phone;
    private String email;
    private List<InventoryLogResponse> totalInventoryLogs;
}
