package com.ejemplo.PruebaAPISpringBoot.service;

import com.ejemplo.PruebaAPISpringBoot.dto.PaymentRequestDto;
import com.ejemplo.PruebaAPISpringBoot.dto.PaymentResponseDto;
import com.ejemplo.PruebaAPISpringBoot.entity.Order;
import com.ejemplo.PruebaAPISpringBoot.repository.OrderRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {
    private final OrderRepository orderRepository;
    private final OrderService orderService;

    public PaymentService(OrderRepository orderRepository, OrderService orderService) {
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }

    @Transactional
    public PaymentResponseDto processPayment(PaymentRequestDto req) {
        Order order = orderRepository.findById(req.getOrderId()).orElse(null);
        if (order == null) {
            return new PaymentResponseDto(false, "Order not found", req.getOrderId());
        }
        if (!"CREATED".equals(order.getStatus())) {
            return new PaymentResponseDto(false, "Order not in payable state", req.getOrderId());
        }
        String card = req.getCardNumber() == null ? "" : req.getCardNumber();
        boolean fail = card.endsWith("0");
        if (fail) {
            order.setStatus("CANCELLED");
            orderRepository.save(order);
            return new PaymentResponseDto(false, "Payment declined (simulated)", req.getOrderId());
        } else {
            orderService.markPaid(order.getId());
            return new PaymentResponseDto(true, "Payment approved (simulated)", req.getOrderId());
        }
    }
}
