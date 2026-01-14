package com.example.FurnitureShop.Service.Interface;

import com.example.FurnitureShop.DTO.Request.InventoryLogRequest;
import com.example.FurnitureShop.DTO.Response.InventoryLogResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface LogService {

    InventoryLogResponse createLog(InventoryLogRequest inventoryLogRequest);
}
