package com.example.FurnitureShop.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_logs")
@Setter @Getter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class InventoryLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id")
    private User staff;

    @Column(name = "quantity_change")
    private Long quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private InventoryLogType type;

    @Column(name = "content")
    private String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id")
    private ProductVariant productVariant;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public enum InventoryLogType{
        IN,OUT
    }
}
