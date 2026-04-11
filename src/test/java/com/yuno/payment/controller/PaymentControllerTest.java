package com.yuno.payment.controller;

import com.yuno.payment.dto.PaymentRequest;
import com.yuno.payment.dto.PaymentResponse;
import com.yuno.payment.service.PaymentService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class PaymentControllerTest {

    @Test
    void shouldCreatePaymentViaController() {

        PaymentService service = Mockito.mock(PaymentService.class);
        PaymentController controller = new PaymentController(service);

        PaymentRequest request = new PaymentRequest(100.0, "INR", "CARD", "k1");

        PaymentResponse mockResponse = new PaymentResponse();
        mockResponse.setStatus("SUCCESS");

        Mockito.when(service.createPayment(request))
                .thenReturn(ResponseEntity.status(201).body(mockResponse));

        ResponseEntity<PaymentResponse> response = controller.createPayment(request);

        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("SUCCESS", response.getBody().getStatus());

        Mockito.verify(service).createPayment(request);
    }
}