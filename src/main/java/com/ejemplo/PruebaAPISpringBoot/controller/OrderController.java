package com.ejemplo.PruebaAPISpringBoot.controller;

import com.ejemplo.PruebaAPISpringBoot.dto.OrderRequestDto;
import com.ejemplo.PruebaAPISpringBoot.entity.Order;
import com.ejemplo.PruebaAPISpringBoot.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    public OrderController(OrderService orderService) { this.orderService = orderService; }

    @PostMapping
    public Mono<ResponseEntity<Order>> createOrder(@RequestBody OrderRequestDto request) {
        log.info("POST /api/orders - createOrder for client '{}'", request.getClientName());
        return Mono.fromCallable(() -> orderService.createOrder(request))
                .subscribeOn(Schedulers.boundedElastic())
                .map(saved -> {
                    log.info("Order created with id={}", saved.getId());
                    return ResponseEntity.ok(saved);
                })
                .onErrorResume(ex -> {
                    log.error("createOrder - error", ex);
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }

    @GetMapping
    public Mono<ResponseEntity<List<Order>>> listOrders() {
        log.info("GET /api/orders - listOrders");
        return Mono.fromCallable(() -> orderService.listOrders())
                .subscribeOn(Schedulers.boundedElastic())
                .map(ResponseEntity::ok)
                .onErrorResume(ex -> {
                    log.error("listOrders - error", ex);
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Object>> getOrder(@PathVariable Long id) {
        log.info("GET /api/orders/{} - getOrder", id);
        return Mono.fromCallable(() -> orderService.getOrder(id))
                .subscribeOn(Schedulers.boundedElastic())
                .map(opt -> opt.<ResponseEntity<Object>>map(order -> {
                    log.info("Order {} found", id);
                    return ResponseEntity.ok(order);
                }).orElseGet(() -> {
                    log.warn("Order {} NOT FOUND", id);
                    return ResponseEntity.status(404).body(Map.of("status",404,"message","Order not found","id", id));
                }))
                .onErrorResume(ex -> {
                    log.error("getOrder - error", ex);
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }

    // endpoint de pago directo sobre orden (alternativa)
    @PostMapping("/{id}/pay")
    public Mono<ResponseEntity<Map<String, Object>>> markPaid(@PathVariable Long id) {
        log.info("POST /api/orders/{}/pay - markPaid", id);
        return Mono.fromCallable(() -> orderService.markPaid(id))
                .subscribeOn(Schedulers.boundedElastic())
                .map(paid -> ResponseEntity.ok(Map.of("status", (Object) "OK", "orderId", (Object) paid.getId(), "newStatus", (Object) paid.getStatus())))
                .onErrorResume(ex -> {
                    log.warn("Error marking order {} as paid: {}", id, ex.getMessage());
                    return Mono.just(ResponseEntity.status(400).body(Map.of("status", (Object) 400, "message", (Object) ex.getMessage())));
                });
    }
}