package com.example.FurnitureShop.Model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id")
    private User staff; // Nhân viên phụ trách giao hàng và lắp đặt

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus;

    @Column(name = "promotion_code")
    private String promotionCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name= "install_status", nullable = false)
    private InstallmentStatus installmentStatus;

    @Column(name = "total", precision = 12, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Column(name = "address")
    private String address;

    @Column(name = "phone_number")
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(name = "note")
    private String note;

    @Column(name = "tracking_number")
    private String trackingNumber;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;

    public enum PaymentMethod{
        COD,
        BANKING
    }
    public enum OrderStatus{
        PENDING,
        DELIVERED,
        SHIPPING,
        CANCELLED,
        PROCESSING
    }

    public enum PaymentStatus{
        UNPAID,
        DEPOSIT,
        PAID,
        PARTIAL,
        REFUND
    }

    public enum InstallmentStatus{
        NOT_INSTALLED,
        INSTALLED
    }

}
