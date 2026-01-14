package com.example.FurnitureShop.DTO.Response;

import com.example.FurnitureShop.Model.InventoryLog;
import com.example.FurnitureShop.Model.InventoryLog.InventoryLogType;
import com.example.FurnitureShop.Model.ProductVariant;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryLogResponse {

    @JsonProperty("staff_name")
    private String staffName;

    private Long quantity;

    @JsonProperty("type")
    private InventoryLogType inventoryLogType;

    private String reason;

    @JsonProperty("variant_name")
    private String variantName;


    public static InventoryLogResponse fromEntity(InventoryLog inventoryLog) {
        ProductVariant productVariant = inventoryLog.getProductVariant();
        return InventoryLogResponse.builder()
                .inventoryLogType(inventoryLog.getType())
                .variantName(productVariant.getHeight() + "x"  + productVariant.getWidth() + "x" + productVariant.getLength())
                .quantity(inventoryLog.getQuantity())
                .staffName(inventoryLog.getStaff().getFullName())
                .reason(inventoryLog.getReason())
                .build();
    }
}
