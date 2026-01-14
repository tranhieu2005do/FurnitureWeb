package com.example.FurnitureShop.Service.Implement;

import com.example.FurnitureShop.DTO.Request.InventoryLogRequest;
import com.example.FurnitureShop.DTO.Request.Kafka.OrderItemDTO;
import com.example.FurnitureShop.DTO.Response.InventoryLogResponse;
import com.example.FurnitureShop.Exception.BussinessException;
import com.example.FurnitureShop.Exception.NotFoundException;
import com.example.FurnitureShop.Model.*;
import com.example.FurnitureShop.Model.InventoryLog.InventoryLogType;
import com.example.FurnitureShop.Repository.InventoryLogRepository;
import com.example.FurnitureShop.Repository.ProductRepository;
import com.example.FurnitureShop.Repository.ProductVariantRepository;
import com.example.FurnitureShop.Repository.UserRepository;
import com.example.FurnitureShop.Service.Interface.LogService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import java.util.List;

@Service
@AllArgsConstructor
@CacheConfig("inventory")
@Transactional
@Slf4j
public class InventoryLogService implements LogService {

    private final InventoryLogRepository logRepository;
    private final UserRepository userRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    @Cacheable(
            key = "T(java.util.Objects).hash(#userId, #productId, #from, #to, #pageable?.pageNumber, #pageable?.pageSize)"
    )
    public Object getLogs(
            Long userId,
            Long productId,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    ) {

        // 1. Filter theo date range
        if (from != null && to != null) {
            return logRepository.findByFiltersAndDate(userId, productId, from, to)
                    .stream()
                    .map(InventoryLogResponse::fromEntity)
                    .toList();
        }

        // 2. Có phân trang
        if (pageable != null && pageable.isPaged()) {
            return logRepository.findByFilters(userId, productId, pageable)
                    .map(InventoryLogResponse::fromEntity);
        }

        // 3. Filter cơ bản
        return logRepository.findByFilters(userId, productId)
                .stream()
                .map(InventoryLogResponse::fromEntity)
                .toList();
    }


    @Override
    @Transactional
    @CacheEvict(allEntries = true)
    public InventoryLogResponse createLog(InventoryLogRequest inventoryLogRequest) {
        User staff = userRepository.findById(inventoryLogRequest.getStaffId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy nhân viên ứng với id"));

        ProductVariant variant = productVariantRepository.findById(inventoryLogRequest.getVariantId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy mẫu sản phẩm ứng với id"));

        if(InventoryLogType.OUT.equals(inventoryLogRequest.getInventoryLogType())){
            if(variant.getInStock() < inventoryLogRequest.getQuantity()){
                throw new BussinessException("So luong san pham hien khong du");
            }
            else{
                variant.setInStock(variant.getInStock() - inventoryLogRequest.getQuantity());
            }
        }

        if(InventoryLogType.IN.equals(inventoryLogRequest.getInventoryLogType())){
            variant.setInStock(variant.getInStock() + inventoryLogRequest.getQuantity());
        }

        InventoryLog inventoryLog = InventoryLog.builder()
                .staff(staff)
                .productVariant(variant)
                .reason(inventoryLogRequest.getReason())
                .quantity(inventoryLogRequest.getQuantity())
                .type(inventoryLogRequest.getInventoryLogType())
                .createdAt(LocalDateTime.now())
                .build();
        logRepository.save(inventoryLog);

        return InventoryLogResponse.fromEntity(inventoryLog);
    }

    public void handleOrderCreated(List<OrderItemDTO> orderItems){
        log.info("handleOrderCreated", orderItems.size());
        for(OrderItemDTO orderItem : orderItems){
            // Trừ instock của variant
            ProductVariant variant = productVariantRepository.findById(orderItem.getVariantId()).get();
            variant.setInStock(variant.getInStock() - orderItem.getQuantity());
            productVariantRepository.save(variant);

            // Trừ instock của tổng product chứa variant
            Product product = variant.getProduct();
            product.setInStockCount(product.getInStockCount() - orderItem.getQuantity());
            productRepository.save(product);

            // Tạo log để lưu lại lịch sử lấy hàng
            InventoryLogRequest request = InventoryLogRequest.builder()
                    .inventoryLogType(InventoryLogType.OUT)
                    .reason("Lấy hàng để đi đơn cho khách.")
                    .variantId(variant.getId())
                    .quantity(orderItem.getQuantity())
                    .build();
            createLog(request);
        }
    }
}
