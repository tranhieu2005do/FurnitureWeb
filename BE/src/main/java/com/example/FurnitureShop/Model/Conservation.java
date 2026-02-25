package com.example.FurnitureShop.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "conversations")
@Setter @Getter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Conservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private User customer;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
