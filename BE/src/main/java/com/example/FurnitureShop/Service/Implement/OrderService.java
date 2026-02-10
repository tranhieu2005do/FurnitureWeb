package com.example.FurnitureShop.Service.Implement;

import com.example.FurnitureShop.DTO.Request.Kafka.OrderItemDTO;
import com.example.FurnitureShop.DTO.Request.NotificationRequest;
import com.example.FurnitureShop.DTO.Request.OrderCreatedMailRequest;
import com.example.FurnitureShop.DTO.Request.OrderRequest;
import com.example.FurnitureShop.DTO.Response.OrderOfShipperResponse;
import com.example.FurnitureShop.DTO.Response.OrderResponse;
import com.example.FurnitureShop.DTO.Response.PageResponse;
import com.example.FurnitureShop.Exception.AuthException;
import com.example.FurnitureShop.Exception.BussinessException;
import com.example.FurnitureShop.Exception.NotFoundException;
import com.example.FurnitureShop.Model.*;
import com.example.FurnitureShop.Model.Order.*;
import com.example.FurnitureShop.Model.Promotion.DiscountType;
import com.example.FurnitureShop.Repository.*;
import com.example.FurnitureShop.Service.Interface.IOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Transactional
@CacheConfig(cacheNames = "orders")
@Slf4j
public class OrderService implements IOrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final UserRepository userRepository;
    private final PromotionRepository promotionRepository;
    private final NotificationService notificationService;
    private final PromotionService promotionService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    @CacheEvict(allEntries = true)
    public OrderResponse createOrderFromCart(Long userId, OrderRequest orderRequest) {
        System.out.println("===== CREATE ORDER METHOD ENTERED =====");
        log.info("Created order from user's cart.");
        User user = userRepository.findById(userId).
                orElseThrow(()-> new NotFoundException("Không tìm thấy người dùng"));
        Cart cart = cartRepository.findByUserId(userId);
        List<CartItem> cartItems = cart.getCartItems();
        if(cartItems == null || cartItems.isEmpty()){
            throw new RuntimeException("Giỏ hàng rỗng");
        }
        User staff = userRepository.findById(orderRequest.getStaffId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy thông tin nhân viên"));
        Order newOrder = Order.builder()
                .user(user)
                .name(orderRequest.getName())
                .phone(orderRequest.getPhone())
                .address(orderRequest.getAddress())
                .note(orderRequest.getNote())
                .orderDate(LocalDateTime.now())
                .orderStatus(OrderStatus.PENDING)
                .installmentStatus(InstallmentStatus.NOT_INSTALLED)
                .paymentMethod(orderRequest.getPaymentMethod())
                .promotionCode(orderRequest.getPromotionCode())
                .paymentStatus(PaymentStatus.UNPAID)
                .staff(staff)
                .build();

        // generate track_number
        String generated = "ORD-"
                + java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + "-" + java.util.UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        newOrder.setTrackingNumber(generated);
        newOrder.setTotalPrice(BigDecimal.ZERO);
        orderRepository.save(newOrder);

        // check quantity_in_stock cho từng sản phẩm
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;
        for(CartItem cartItem : cartItems){
            ProductVariant variant = productVariantRepository.findById(cartItem.getProductVariant().getId()).get();
            if(variant.getInStock() < cartItem.getQuantity()){
                throw new BussinessException("Số lượng sản phẩm hiện tại không đủ!!");
            }
            OrderItem orderItem = OrderItem.builder()
                    .order(newOrder)
                    .productVariant(variant)
                    .quantity(cartItem.getQuantity())
                    .price(variant.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                    .build();
            totalPrice = totalPrice.add(orderItem.getPrice());
            orderItems.add(orderItem);
        }
//        cartItemRepository.deleteAll(cartItems);

        BigDecimal realPrice = totalPrice;
        BigDecimal discount = BigDecimal.ZERO;
        int Discount = 0;
        // check xem có mã giảm giá không và có hợp lệ không
        if(orderRequest.getPromotionCode() != null && orderRequest.getPromotionCode().length() != 0){
            Promotion promotion = promotionRepository.findByCode(orderRequest.getPromotionCode());
            Pair<BigDecimal,BigDecimal> res = promotionService.applyPromotion(promotion, totalPrice, cartItems, userId);
            log.info("TotalPrice and Discount {}", res.toString());
            realPrice = res.getFirst();
            discount = res.getSecond();
            log.info("Discount {}", discount);
            log.info("RealPrice {}", realPrice);
        }
        newOrder.setOrderItems(orderItems);
        newOrder.setTotalPrice(totalPrice);
        orderRepository.save(newOrder);
        orderItemRepository.saveAll(orderItems);

        // Tạo Request để mail thông báo đặt hàng thành công qua mail
        OrderCreatedMailRequest mailRequest = OrderCreatedMailRequest.builder()
                .content("Thông báo đơn hàng đặt thành công!!")
                .subject("ĐẶT HÀNG THÀNH CÔNG")
                .to(user.getEmail())
                .build();

        List<OrderItemDTO> orderItemDTOS = orderItems.stream().map(OrderItemDTO::fromEntity).toList();
        mailRequest.setOrderItems(orderItemDTOS);
        Map<String,Object> props = new HashMap<>();
        props.put("fullName", user.getFullName());
        props.put("email", user.getEmail());
        props.put("phone", user.getPhone());
        props.put("address", user.getAddress());
        props.put("realPrice", realPrice);
        props.put("salePercentage", Discount);
        props.put("discount", discount);
        props.put("trackNumber", generated);
        props.put("totalPrice", totalPrice);
        mailRequest.setProps(props);
        System.out.println("===== SYSTEM OUT =====");
        log.error("===== ERROR LOG =====");
        log.warn("===== WARN LOG =====");
        log.info("===== INFO LOG =====");
        log.info("MailRequest before Kafka: {}", mailRequest);
        log.info("OrderItems size: {}", mailRequest.getOrderItems().size());
        NotificationRequest orderedNotifi = NotificationRequest.builder()
                .message("Đơn hàng của bạn đã được đặt và chờ được tiến hành.")
                .topic("Đặt hàng thành công.")
                .userId(userId)
                .build();
        mailRequest.setNotificationRequest(orderedNotifi);
//        kafkaTemplate.send("Order_Created", mailRequest);
        return OrderResponse.fromEntity(newOrder);
    }

    @Override
    @Cacheable(
            key = "T(java.util.Objects).hash(#userId, #pageable.pageNumber, #pageabe.pageSize, #startDate, #endDate)"
    )
    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> getOrdersByUserId(
            Long userId,
            Pageable pageable,
            LocalDate startDate,
            LocalDate endDate)
    {
        return PageResponse.fromPage(
                orderRepository.getOrdersByUserId(userId, pageable, startDate, endDate)
                .map(OrderResponse::fromEntity));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            key = "T(java.util.Objects).hash(#userId, #pageable.pageNumber, #pageable.pageSize, #startDate, #endDate)"
    )
    public PageResponse<OrderResponse> getOrders(
            Pageable pageable,
            LocalDate startDate,
            LocalDate endDate)
    {
        return PageResponse.fromPage(orderRepository.getOrders(pageable, startDate, endDate)
                .map(OrderResponse::fromEntity));
    }

    @Override
    @CacheEvict(allEntries = true)
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus orderStatus) {
        Order order = orderRepository.findById(orderId).get();
        if(orderStatus.equals(OrderStatus.CANCELLED)){
            throw new BussinessException("Không thể huỷ đơn hàng!!");
        }
        order.setOrderStatus(orderStatus);
        orderRepository.save(order);
        return OrderResponse.fromEntity(order);
    }

    @CacheEvict(allEntries = true)
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).get();
        if(order.getOrderStatus().equals(OrderStatus.CANCELLED)){
            throw new BussinessException("Không thể huỷ đơn hàng!!");
        }
        List<OrderItem> orderItems = order.getOrderItems();
        for(OrderItem orderItem : orderItems){
            ProductVariant variant = productVariantRepository.findById(orderItem.getProductVariant().getId()).get();
            variant.setInStock(variant.getInStock() + orderItem.getQuantity());
            productVariantRepository.save(variant);
        }
        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    @Override
    @CacheEvict(allEntries = true)
    public void deleteOrder(Long orderId){
        Order order = orderRepository.findById(orderId).get();
        List<OrderItem> orderItems = order.getOrderItems();
        for(OrderItem orderItem : orderItems){
            orderRepository.deleteById(orderItem.getId());
        }
        orderRepository.deleteById(orderId);
    }

    @CacheEvict(allEntries = true)
    public OrderResponse reOrder(Long userId, Long orderId){
        User user =  userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng."));
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đơn hàng tương ứng id: " + orderId));

        if(order.getUser().getId() != user.getId()){
            throw new AuthException("Đơn hàng này không phải của người dùng " + userId + ". Không thể đặt lại đơn hàng.");
        }

        List<OrderItem> orderItems = order.getOrderItems();
        for(OrderItem orderItem : orderItems){
            ProductVariant variant = productVariantRepository.findById(orderItem.getProductVariant().getId()).get();
            if(variant.getInStock() < orderItem.getQuantity()){
                throw new BussinessException("Số lượng sản phẩm không đủ");
            }
        }
        for(OrderItem orderItem : orderItems){
            ProductVariant variant = productVariantRepository.findById(orderItem.getProductVariant().getId()).get();
            variant.setInStock(variant.getInStock() - orderItem.getQuantity());
            productVariantRepository.save(variant);
        }
        Order new_order = Order.builder()
                .staff(order.getStaff())
                .orderDate(LocalDateTime.now())
                .orderStatus(OrderStatus.PENDING)
                .orderItems(orderItems)
                .phone(order.getPhone())
                .address(order.getAddress())
                .note(order.getNote())
                .installmentStatus(InstallmentStatus.NOT_INSTALLED)
                .totalPrice(order.getTotalPrice())
                .build();
        String generated = "ORD-"
                + java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + "-" + java.util.UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        new_order.setTrackingNumber(generated);
        orderRepository.save(new_order);
        return OrderResponse.fromEntity(new_order);
    }

    @Transactional(readOnly = true)
    @Cacheable(
            key = "T(java.util.Objects).hash(#pageable.pageNumber, #pageable.pageSize, #shipperId)"
    )
    public PageResponse<OrderOfShipperResponse> getOrdersInstalledByShipperId(Pageable pageable, Long shipperId){
        return PageResponse.fromPage(
                orderRepository.findByShipperId(pageable, shipperId)
                .map(OrderOfShipperResponse::fromEntity));
    }
}
