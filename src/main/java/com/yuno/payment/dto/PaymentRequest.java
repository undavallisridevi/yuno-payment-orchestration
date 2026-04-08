package com.yuno.payment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
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