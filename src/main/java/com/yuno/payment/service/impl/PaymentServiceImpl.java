package com.yuno.payment.service.impl;

import java.time.LocalDateTime;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.yuno.payment.dto.PaymentRequest;
import com.yuno.payment.dto.PaymentResponse;
import com.yuno.payment.model.Payment;
import com.yuno.payment.orchestration.PaymentOrchestrator;
import com.yuno.payment.repository.PaymentRepository;
import com.yuno.payment.service.PaymentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
	

    private final PaymentRepository paymentRepository;
    private final PaymentOrchestrator orchestrator;

    @Override
    public ResponseEntity<PaymentResponse> createPayment(PaymentRequest request) {

        // STEP 1: Check idempotency
        var existing = paymentRepository.findByIdempotencyKey(request.getIdempotencyKey());

        if (existing.isPresent()) {
            Payment payment = existing.get();

            PaymentResponse response = PaymentResponse.builder()
                    .paymentId(payment.getId())
                    .status(payment.getStatus())
                    .provider(payment.getProvider())
                    .build();

            //CASE: Still processing
            if ("PROCESSING".equals(payment.getStatus())) {
                return ResponseEntity.accepted().body(response); // 202
            }

            // CASE: Already completed (SUCCESS / FAILED)
            return ResponseEntity.ok(response); // 200
        }

        try {
            // STEP 2: Create new payment
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

            // STEP 3: Orchestration
            saved = orchestrator.process(saved);

            paymentRepository.save(saved);

            PaymentResponse response = PaymentResponse.builder()
                    .paymentId(saved.getId())
                    .status(saved.getStatus())
                    .provider(saved.getProvider())
                    .build();

            //First time creation → 201
            return ResponseEntity.status(201).body(response);

        } catch (DataIntegrityViolationException e) {

            // STEP 4: Race condition handling
            Payment payment = paymentRepository
                    .findByIdempotencyKey(request.getIdempotencyKey())
                    .orElseThrow(() -> new RuntimeException("Payment exists but not found"));

            PaymentResponse response = PaymentResponse.builder()
                    .paymentId(payment.getId())
                    .status(payment.getStatus())
                    .provider(payment.getProvider())
                    .build();

            return ResponseEntity.ok(response); // 200
        }
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