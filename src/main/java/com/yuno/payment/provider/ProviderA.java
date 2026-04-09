package com.yuno.payment.provider;

import com.yuno.payment.exception.PaymentProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component("PROVIDER_A")
@Slf4j
public class ProviderA implements PaymentProvider {

    @Override
    @Retryable(
            retryFor = PaymentProcessingException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 200, multiplier = 2)
    )
    public void processPayment(String paymentId) {

        boolean success = new Random().nextBoolean();

        if (!success) {
            log.warn("Provider A failed for payment {}", paymentId);
            throw new PaymentProcessingException("Provider A failed");
        }

        log.info("Provider A success for payment {}", paymentId);
    }

    @Recover
    public void recover(PaymentProcessingException ex, String paymentId) {
        log.error("All retries failed for payment {} in Provider A", paymentId);
        throw ex; // propagate to orchestrator
    }
}