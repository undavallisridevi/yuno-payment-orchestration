package com.yuno.payment.routing;

import org.springframework.stereotype.Component;

@Component
public class PaymentRouterImpl implements PaymentRouter {

    @Override
    public String route(String method) {
        return switch (method) {
            case "CARD" -> "PROVIDER_A";
            case "UPI" -> "PROVIDER_B";
            default -> throw new RuntimeException("Unsupported payment method");
        };
    }
}