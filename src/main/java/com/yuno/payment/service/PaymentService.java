package com.yuno.payment.service;

import org.springframework.http.ResponseEntity;

import com.yuno.payment.dto.PaymentRequest;
import com.yuno.payment.dto.PaymentResponse;

public interface PaymentService {

	 ResponseEntity<PaymentResponse> createPayment(PaymentRequest request);

    PaymentResponse getPayment(String id);
}