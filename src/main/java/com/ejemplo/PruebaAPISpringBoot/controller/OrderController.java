package com.ejemplo.PruebaAPISpringBoot.controller;

import com.ejemplo.PruebaAPISpringBoot.dto.OrderRequestDto;
import com.ejemplo.PruebaAPISpringBoot.entity.Order;
import com.ejemplo.PruebaAPISpringBoot.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    public OrderController(OrderService orderService) { this.orderService = orderService; }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequestDto request) {
        log.info("POST /api/orders - createOrder for client '{}'", request.getClientName());
        Order saved = orderService.createOrder(request);
        log.info("Order created with id={}", saved.getId());
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<Order>> listOrders() {
        log.info("GET /api/orders - listOrders");
        return ResponseEntity.ok(orderService.listOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getOrder(@PathVariable Long id) {
        log.info("GET /api/orders/{} - getOrder", id);
        return orderService.getOrder(id)
                .<ResponseEntity<Object>>map(order -> {
                    log.info("Order {} found", id);
                    return ResponseEntity.ok(order);
                })
                .orElseGet(() -> {
                    log.warn("Order {} NOT FOUND", id);
                    return ResponseEntity.status(404).body(Map.of("status",404,"message","Order not found","id", id));
                });
    }

    // endpoint de pago directo sobre orden (alternativa)
    @PostMapping("/{id}/pay")
    public ResponseEntity<Object> markPaid(@PathVariable Long id) {
        log.info("POST /api/orders/{}/pay - markPaid", id);
        try {
            Order paid = orderService.markPaid(id);
            return ResponseEntity.ok(Map.of("status","OK","orderId", paid.getId(), "newStatus", paid.getStatus()));
        } catch (RuntimeException ex) {
            log.warn("Error marking order {} as paid: {}", id, ex.getMessage());
            return ResponseEntity.status(400).body(Map.of("status",400,"message", ex.getMessage()));
        }
    }
}