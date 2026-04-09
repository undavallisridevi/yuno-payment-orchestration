package com.yuno.payment.orchestration;

import com.yuno.payment.exception.PaymentProcessingException;
import com.yuno.payment.model.Payment;
import com.yuno.payment.provider.PaymentProvider;
import com.yuno.payment.routing.PaymentRouter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PaymentOrchestratorImpl implements PaymentOrchestrator {

    private final PaymentRouter router;
    private final Map<String, PaymentProvider> providers;

    @Override
    public Payment process(Payment payment) {

        String providerKey = router.route(payment.getMethod());
        PaymentProvider provider = providers.get(providerKey);

        payment.setProvider(providerKey);
        payment.setStatus("PROCESSING");

        try {
            provider.processPayment(payment.getId());
            payment.setStatus("SUCCESS");

        } catch (PaymentProcessingException ex) {
            payment.setStatus("FAILED");
        }

        return payment;
    }
}