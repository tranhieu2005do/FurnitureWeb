package com.example.FurnitureShop.Controller;

import com.example.FurnitureShop.DTO.Request.OrderRequest;
import com.example.FurnitureShop.DTO.Response.ApiResponse;
import com.example.FurnitureShop.DTO.Response.OrderResponse;
import com.example.FurnitureShop.DTO.Response.PageResponse;
import com.example.FurnitureShop.Model.Order.OrderStatus;
import com.example.FurnitureShop.Service.Implement.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PreAuthorize("@userService.isSelf(#userId)")
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrderFromCart(
            @RequestParam Long userId,
            @RequestBody @Valid OrderRequest orderRequest)
    {
        OrderResponse orderResponse = orderService.createOrderFromCart(userId, orderRequest);
        return ResponseEntity.ok(ApiResponse.<OrderResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Create order successfully")
                        .data(orderResponse)
                        .build());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getOrders(
            Pageable pageable,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false ) LocalDate endDate
    ){
        return ResponseEntity.ok(ApiResponse.<PageResponse<OrderResponse>>builder()
                .data(orderService.getOrders(pageable, startDate, endDate))
                .statusCode(HttpStatus.OK.value())
                .message("Get orders successfully")
                .build());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF') or (@userService.isSelf(#userId)))")
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getOrdersByUserId(
            @PathVariable Long userId,
            Pageable pageable,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate)
    {
        return ResponseEntity.ok(ApiResponse.<PageResponse<OrderResponse>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(orderService.getOrdersByUserId(userId, pageable, startDate, endDate))
                        .message("Get orders by user id: " + userId)
                        .build());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @PatchMapping("/update/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(@PathVariable Long orderId, @RequestParam OrderStatus orderStatus){
        return ResponseEntity.ok(ApiResponse.<OrderResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Update order status")
                        .data(orderService.updateOrderStatus(orderId, orderStatus))
                        .build());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF') or (@userService.isSelf(#userId)))")
    @PatchMapping("/cancelled/{orderId}")
    public ResponseEntity<ApiResponse<Void>> cancelledOrder(@PathVariable Long orderId){
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Cancel order")
                        .build());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable Long orderId){
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Delete order")
                .build());
    }

    @PreAuthorize("@userService.isSef(#userId)")
    @PostMapping("/{orderId}/reorder")
    public ResponseEntity<ApiResponse<OrderResponse>> reOrder(
            @PathVariable Long orderId,
            @RequestParam Long userId
    ){
        return ResponseEntity.ok(ApiResponse.<OrderResponse>builder()
                .message("Order reorder")
                .data(orderService.reOrder(orderId, userId))
                .statusCode(HttpStatus.CREATED.value())
                .build());
    }
}
