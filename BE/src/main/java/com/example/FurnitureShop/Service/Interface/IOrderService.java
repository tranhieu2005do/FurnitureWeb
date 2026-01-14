package com.example.FurnitureShop.Service.Interface;

import com.example.FurnitureShop.DTO.Request.OrderRequest;
import com.example.FurnitureShop.DTO.Response.OrderResponse;
import com.example.FurnitureShop.DTO.Response.PageResponse;
import com.example.FurnitureShop.Model.Order.OrderStatus;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;

public interface IOrderService {
    PageResponse<OrderResponse> getOrders(Pageable pageable, LocalDate startDate, LocalDate endDate);

    OrderResponse createOrderFromCart(Long userId, OrderRequest orderRequest);

    PageResponse<OrderResponse> getOrdersByUserId(Long userId, Pageable pageable, LocalDate startDate, LocalDate endDate);

    OrderResponse updateOrderStatus(Long orderId, OrderStatus orderStatus);

    void deleteOrder(Long orderId);
}
