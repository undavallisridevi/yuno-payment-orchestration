package com.yuno.payment.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;

import com.yuno.payment.dto.PaymentRequest;
import com.yuno.payment.dto.PaymentResponse;
import com.yuno.payment.model.Payment;
import com.yuno.payment.orchestration.PaymentOrchestrator;
import com.yuno.payment.repository.PaymentRepository;
import com.yuno.payment.service.impl.PaymentServiceImpl;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentOrchestrator orchestrator;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @BeforeEach
    void setup() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void shouldCreatePaymentSuccessfully() {

        PaymentRequest request = new PaymentRequest(100.0, "INR", "CARD", "key1");

        String redisKey = "payment:key1";

        when(valueOperations.get(redisKey)).thenReturn(null);
        when(paymentRepository.findByIdempotencyKey("key1")).thenReturn(Optional.empty());

        Payment payment = Payment.builder()
                .id("1")
                .status("SUCCESS")
                .provider("PROVIDER_A")
                .build();

        when(paymentRepository.save(any())).thenReturn(payment);
        when(orchestrator.process(any())).thenReturn(payment);

        ResponseEntity<PaymentResponse> response = paymentService.createPayment(request);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals("SUCCESS", response.getBody().getStatus());

        verify(valueOperations).set(eq(redisKey), any(), any());
    }

    @Test
    void shouldReturnDuplicateResponse() {

        PaymentRequest request = new PaymentRequest(100.0, "INR", "CARD", "dup");

        String redisKey = "payment:dup";

        when(valueOperations.get(redisKey)).thenReturn(null);

        Payment existing = Payment.builder()
                .id("1")
                .status("SUCCESS")
                .provider("PROVIDER_A")
                .build();

        when(paymentRepository.findByIdempotencyKey("dup"))
                .thenReturn(Optional.of(existing));

        ResponseEntity<PaymentResponse> response = paymentService.createPayment(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("SUCCESS", response.getBody().getStatus());

        verify(orchestrator, never()).process(any());
    }

    @Test
    void shouldReturnProcessingStatus() {

        PaymentRequest request = new PaymentRequest(100.0, "INR", "CARD", "processing");

        String redisKey = "payment:processing";

        when(valueOperations.get(redisKey)).thenReturn(null);

        Payment existing = Payment.builder()
                .id("1")
                .status("PROCESSING")
                .provider("PROVIDER_A")
                .build();

        when(paymentRepository.findByIdempotencyKey("processing"))
                .thenReturn(Optional.of(existing));

        ResponseEntity<PaymentResponse> response = paymentService.createPayment(request);

        assertEquals(202, response.getStatusCodeValue());

        verify(orchestrator, never()).process(any());
    }

    @Test
    void shouldReturnFromRedisCache() {

        PaymentRequest request = new PaymentRequest(100.0, "INR", "CARD", "cache");

        String redisKey = "payment:cache";

        PaymentResponse cached = new PaymentResponse();
        cached.setStatus("SUCCESS");

        when(valueOperations.get(redisKey)).thenReturn(cached);

        ResponseEntity<PaymentResponse> response = paymentService.createPayment(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("SUCCESS", response.getBody().getStatus());

        verify(paymentRepository, never()).findByIdempotencyKey(any());
    }
}