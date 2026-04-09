package com.yuno.payment.provider;

public interface PaymentProvider {

    void processPayment(String paymentId);
}