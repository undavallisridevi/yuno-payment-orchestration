package com.yuno.payment.service.impl;

import com.yuno.payment.dto.PaymentRequest;
import com.yuno.payment.dto.PaymentResponse;
import com.yuno.payment.model.Payment;
import com.yuno.payment.repository.PaymentRepository;
import com.yuno.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Override
    public PaymentResponse createPayment(PaymentRequest request) {

        Payment payment = Payment.builder()
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .method(request.getMethod())
                .status("CREATED")
                .idempotencyKey(request.getIdempotencyKey())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Payment saved = paymentRepository.save(payment);

        return PaymentResponse.builder()
                .paymentId(saved.getId())
                .status(saved.getStatus())
                .provider(saved.getProvider())
                .build();
    }

    @Override
    public PaymentResponse getPayment(String id) {

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        return PaymentResponse.builder()
                .paymentId(payment.getId())
                .status(payment.getStatus())
                .provider(payment.getProvider())
                .build();
    }
}