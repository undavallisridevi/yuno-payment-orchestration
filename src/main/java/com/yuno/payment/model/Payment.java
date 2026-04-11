package com.yuno.payment.model;

import java.time.LocalDateTime;

import org.springframework.data.redis.core.RedisHash;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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