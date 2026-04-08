package com.yuno.payment.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private Double amount;

    private String currency;

    private String method; // CARD / UPI

    private String status; // CREATED, SUCCESS, FAILED

    private String provider; // A / B

    @Column(unique = true)
    private String idempotencyKey;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}