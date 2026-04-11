package com.yuno.payment.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
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

public class PaymentServiceImpl implements PaymentService {
	

    private final PaymentRepository paymentRepository;
    private final PaymentOrchestrator orchestrator;
    private final RedisTemplate<String, Object> redisTemplate;
    
    public PaymentServiceImpl(PaymentRepository paymentRepository,
            PaymentOrchestrator orchestrator,
            RedisTemplate<String, Object> redisTemplate) {
this.paymentRepository = paymentRepository;
this.orchestrator = orchestrator;
this.redisTemplate = redisTemplate;
}

    @Override
    public ResponseEntity<PaymentResponse> createPayment(PaymentRequest request) {

        String redisKey = "payment:" + request.getIdempotencyKey();

        //STEP 1: Check Redis (FAST PATH)
        PaymentResponse cached = (PaymentResponse) redisTemplate.opsForValue().get(redisKey);

        if (cached != null) {
            return ResponseEntity.ok(cached); // 200
        }

        // STEP 2: Check DB
        var existing = paymentRepository.findByIdempotencyKey(request.getIdempotencyKey());

        if (existing.isPresent()) {
            Payment payment = existing.get();

            PaymentResponse response = PaymentResponse.builder()
                    .paymentId(payment.getId())
                    .status(payment.getStatus())
                    .provider(payment.getProvider())
                    .build();

            // Save to Redis for future fast access
            redisTemplate.opsForValue().set(redisKey, response);

            if ("PROCESSING".equals(payment.getStatus())) {
                return ResponseEntity.accepted().body(response); // 202
            }

            return ResponseEntity.ok(response); // 200
        }

        try {
            // STEP 3: Create payment
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

            // STEP 4: Orchestrate
            saved = orchestrator.process(saved);

            paymentRepository.save(saved);

            PaymentResponse response = PaymentResponse.builder()
                    .paymentId(saved.getId())
                    .status(saved.getStatus())
                    .provider(saved.getProvider())
                    .build();

            // STEP 5: Cache result in Redis
            redisTemplate.opsForValue().set(redisKey, response, Duration.ofMinutes(10));
            return ResponseEntity.status(201).body(response);

        } catch (DataIntegrityViolationException e) {

            Payment payment = paymentRepository
                    .findByIdempotencyKey(request.getIdempotencyKey())
                    .orElseThrow();

            PaymentResponse response = PaymentResponse.builder()
                    .paymentId(payment.getId())
                    .status(payment.getStatus())
                    .provider(payment.getProvider())
                    .build();

            redisTemplate.opsForValue().set(redisKey, response);

            return ResponseEntity.ok(response);
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