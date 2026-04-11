package com.yuno.payment.routing;

import org.springframework.stereotype.Component;

import com.yuno.payment.exception.InvalidPaymentMethodException;

@Component
public class PaymentRouterImpl implements PaymentRouter {

    @Override
    public String route(String method) {
        return switch (method) {
            case "CARD" -> "PROVIDER_A";
            case "UPI" -> "PROVIDER_B";
            default -> throw new InvalidPaymentMethodException("Unsupported payment method");
        };
    }
}