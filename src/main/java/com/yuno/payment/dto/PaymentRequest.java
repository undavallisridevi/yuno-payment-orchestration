package com.yuno.payment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    @NotNull
    private Double amount;

    @NotNull
    private String currency;

    @NotNull
    private String method;

    @NotNull
    private String idempotencyKey;
}