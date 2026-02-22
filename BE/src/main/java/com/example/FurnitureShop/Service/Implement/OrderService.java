package com.example.FurnitureShop.Service.Implement;

import com.example.FurnitureShop.DTO.Request.CartItemRequest;
import com.example.FurnitureShop.DTO.Request.Kafka.OrderItemDTO;
import com.example.FurnitureShop.DTO.Request.NotificationRequest;
import com.example.FurnitureShop.DTO.Request.OrderCreatedMailRequest;
import com.example.FurnitureShop.DTO.Request.OrderRequest;
import com.example.FurnitureShop.DTO.Response.*;
import com.example.FurnitureShop.DTO.Response.OrderCreatedResponse.Item;
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
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PromotionRepository promotionRepository;
    private final NotificationService notificationService;
    private final PromotionService promotionService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    @Override
    @CacheEvict(allEntries = true)
    public OrderCreatedResponse createOrderFromCart(Long userId, OrderRequest orderRequest) {
        System.out.println("===== CREATE ORDER METHOD ENTERED =====");
        log.info("Created order from user's cart.");
        User user = userRepository.findById(userId).
                orElseThrow(()-> new NotFoundException("Không tìm thấy người dùng"));
        Cart cart = cartRepository.findByUserId(userId);
        List<CartItem> cartItems = cart.getCartItems();
        if(cartItems == null || cartItems.isEmpty()){
            throw new RuntimeException("Giỏ hàng rỗng");
        }
        Order newOrder = Order.builder()
                .user(user)
                .name(user.getFullName())
                .phone(user.getPhone())
                .address(user.getAddress())
                .note(orderRequest.getNote())
                .orderDate(LocalDateTime.now())
                .orderStatus(OrderStatus.PENDING)
                .installmentStatus(InstallmentStatus.NOT_INSTALLED)
                .paymentMethod(orderRequest.getPaymentMethod())
                .promotionCodes(orderRequest.getPromotionCodes())
                .paymentStatus(PaymentStatus.UNPAID)
                .build();
        // generate track_number
        String generated = "ORD-"
                + java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + "-" + java.util.UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        newOrder.setTrackingNumber(generated);
        newOrder.setTotalPrice(BigDecimal.ZERO);
        orderRepository.save(newOrder);

        //OrderCreatedResponse
        OrderCreatedResponse response = OrderCreatedResponse.builder()
                .email(user.getEmail())
                .phone(user.getPhone())
                .customerName(user.getFullName())
                .note(orderRequest.getNote())
                .trackingNumber(newOrder.getTrackingNumber())
                .address(user.getAddress())
                .build();

        // check quantity_in_stock cho từng sản phẩm
        List<OrderItem> orderItems = new ArrayList<>();
        List<Item> items = new ArrayList<>();
        BigDecimal subTotalPrice = BigDecimal.ZERO;
        for(CartItemRequest itemRequest : orderRequest.getItemRequest()){
            CartItem cartItem = cartItemRepository
                    .findByCartItemIdAndVariantId(itemRequest.getCartId(), itemRequest.getVariantId());

            // Xử lý in_stock cho variant và tổng variant trong product
            ProductVariant variant = cartItem.getProductVariant();
            variant.setInStock(variant.getInStock() - itemRequest.getQuantity());
            productVariantRepository.save(variant);
            Product product = variant.getProduct();
            product.setInStockCount(product.getInStockCount() - itemRequest.getQuantity());
            productRepository.save(product);

            // item trong orderCreatedResponse
            items.add(Item.fromCartItem(cartItem));

            // item trong Order
            orderItems.add(OrderItem.builder()
                    .order(newOrder)
                    .price(cartItem.getProductVariant().getPrice())
                    .quantity(cartItem.getQuantity())
                    .build());

            //add price to totalPrice
            subTotalPrice = subTotalPrice.add(cartItem.getProductVariant().getPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity())));
//            cartItemRepository.delete(cartItem);
        }
        newOrder.setOrderItems(orderItems);
        response.setItems(items);

        // Xử lý promotion
        BigDecimal totalDisconut = BigDecimal.ZERO;
        List<ApplyPromotionResponse> applyPromotionResponses = new ArrayList<>();
        for(String code: orderRequest.getPromotionCodes()){
            Promotion promotion = promotionRepository.findByCode(code);
            if (promotion == null) {
                throw new NotFoundException("Voucher không tồn tại");
            }
            ApplyPromotionResponse promotionResponse = promotionService.applyPromotion(promotion, cartItems, subTotalPrice, newOrder.getId(), user);
            applyPromotionResponses.add(promotionResponse);
            totalDisconut = totalDisconut.add(promotionResponse.getDiscountValue());
        }
        response.setApplyPromotion(applyPromotionResponses);
        BigDecimal finalPrice = subTotalPrice.subtract(totalDisconut);
        newOrder.setTotalPrice(finalPrice);
        orderRepository.save(newOrder);
        response.setSubTotal(subTotalPrice);
        response.setFinalPrice(finalPrice);
        response.setTotalDiscount(totalDisconut);

        // Tạo Request để mail thông báo đặt hàng thành công qua mail
        OrderCreatedMailRequest mailRequest = OrderCreatedMailRequest.builder()
                .content("Thông báo đơn hàng đặt thành công!!")
                .subject("ĐẶT HÀNG THÀNH CÔNG")
                .to(user.getEmail())
                .build();
        NotificationRequest orderedNotifi = NotificationRequest.builder()
                .message("Đơn hàng của bạn đã được đặt và chờ được tiến hành.")
                .topic("Đặt hàng thành công.")
                .userId(userId)
                .build();

        return response;
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
