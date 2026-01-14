package com.example.FurnitureShop.DTO.Request;

import com.example.FurnitureShop.Model.InventoryLog.InventoryLogType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryLogRequest {
    @JsonProperty("staff_id")
//    @NotNull(message = "Thiếu mã định danh người dùng.")
    private Long staffId;

    @NotBlank(message = "Cần có lý do chính đáng.")
    private String reason;

    @JsonProperty("type")
    @NotBlank
    private InventoryLogType inventoryLogType;

    @NotNull(message = "Số lượng hàng là bao nhiêu.")
    private long quantity;

    @JsonProperty("variant_id")
    @NotNull(message = "Thiếu mã sản phẩm.")
    private Long variantId;
}
