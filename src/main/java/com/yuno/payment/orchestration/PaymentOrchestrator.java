package com.yuno.payment.orchestration;

import com.yuno.payment.model.Payment;

public interface PaymentOrchestrator {
    Payment process(Payment payment);
}