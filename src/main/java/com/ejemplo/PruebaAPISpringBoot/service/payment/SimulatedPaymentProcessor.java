package com.ejemplo.PruebaAPISpringBoot.service.payment;

import com.ejemplo.PruebaAPISpringBoot.dto.PaymentRequestDto;

import org.springframework.stereotype.Component;
@Component
public class SimulatedPaymentProcessor implements PaymentProcessor {

    @Override
    public PaymentResult process(PaymentRequestDto request) {
        String card = request.getCardNumber() == null ? "" : request.getCardNumber();
        boolean fail = card.endsWith("0");
        if (fail) {
            return new PaymentResult(false, "Payment declined (simulated)");
        } else {
            return new PaymentResult(true, "Payment approved (simulated)");
        }
    }
}
