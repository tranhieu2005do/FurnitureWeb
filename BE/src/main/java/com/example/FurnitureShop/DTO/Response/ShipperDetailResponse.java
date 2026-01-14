package com.example.FurnitureShop.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ShipperDetailResponse {
    private String shipperName;
    private String shipperPhone;
    private String shipperEmail;
    private PageResponse<OrderOfShipperResponse> orderOfShipper;
}
