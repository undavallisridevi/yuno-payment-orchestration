package com.yuno.payment.provider;
import java.util.Random;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import com.yuno.payment.exception.PaymentProcessingException;

import lombok.extern.slf4j.Slf4j;

@Component("PROVIDER_B")
@Slf4j
public class ProviderB implements PaymentProvider
{

    @Override
    @Retryable(
            retryFor = PaymentProcessingException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 200, multiplier = 2)
    )
    public void processPayment(String paymentId) {

        boolean success = new Random().nextBoolean();

        if (!success) {
            log.warn("Provider B failed for payment {}", paymentId);
            throw new PaymentProcessingException("Provider A failed");
        }

        log.info("Provider B success for payment {}", paymentId);
    }
    

    @Recover
    public void recover(PaymentProcessingException ex, String paymentId) {
        throw ex;
    }
}