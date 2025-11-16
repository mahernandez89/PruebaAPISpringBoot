package com.ejemplo.PruebaAPISpringBoot.controller;

import com.ejemplo.PruebaAPISpringBoot.dto.PaymentRequestDto;
import com.ejemplo.PruebaAPISpringBoot.dto.PaymentResponseDto;
import com.ejemplo.PruebaAPISpringBoot.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) { this.paymentService = paymentService; }

    @PostMapping("/pay")
    public ResponseEntity<PaymentResponseDto> pay(@RequestBody PaymentRequestDto req) {
        PaymentResponseDto resp = paymentService.processPayment(req);
        if (resp.isSuccess()) return ResponseEntity.ok(resp);
        return ResponseEntity.badRequest().body(resp);
    }
}
