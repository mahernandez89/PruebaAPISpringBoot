package com.ejemplo.PruebaAPISpringBoot.service.payment;

import com.ejemplo.PruebaAPISpringBoot.dto.PaymentRequestDto;

public interface PaymentProcessor {
    PaymentResult process(PaymentRequestDto request);
}
