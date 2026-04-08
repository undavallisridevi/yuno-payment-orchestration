package com.yuno.payment.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentResponse {

    private String paymentId;
    private String status;
    private String provider;
}