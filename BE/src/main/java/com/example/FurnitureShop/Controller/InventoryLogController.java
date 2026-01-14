package com.example.FurnitureShop.Controller;

import com.example.FurnitureShop.DTO.Request.InventoryLogRequest;
import com.example.FurnitureShop.DTO.Response.ApiResponse;
import com.example.FurnitureShop.DTO.Response.InventoryLogResponse;
import com.example.FurnitureShop.Service.Implement.InventoryLogService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
//import org.apache.kafka.shaded.com.google.protobuf.Api;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory-logs")
@AllArgsConstructor
public class InventoryLogController {
    private final InventoryLogService inventoryLogService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getLogs(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long productId,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime from,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime to,

            Pageable pageable)
    {
        return ResponseEntity.ok(ApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Success")
                .data(inventoryLogService.getLogs(userId, productId, from, to, pageable))
                .build());
    }

    @PreAuthorize("hasRole('Ware House Manager')")
    @PostMapping
    public ResponseEntity<ApiResponse<InventoryLogResponse>> createLog(
            @RequestBody @Valid InventoryLogRequest inventoryLogRequest)
    {
        return ResponseEntity.ok(ApiResponse.<InventoryLogResponse>builder()
                .data(inventoryLogService.createLog(inventoryLogRequest))
                .statusCode(HttpStatus.OK.value())
                .message("Success")
                .build());
    }
}
