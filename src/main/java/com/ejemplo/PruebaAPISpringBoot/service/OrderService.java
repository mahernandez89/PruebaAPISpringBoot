package com.ejemplo.PruebaAPISpringBoot.service;


import com.ejemplo.PruebaAPISpringBoot.dto.OrderRequestDto;
import com.ejemplo.PruebaAPISpringBoot.dto.ProductDto;
import com.ejemplo.PruebaAPISpringBoot.entity.Order;
import com.ejemplo.PruebaAPISpringBoot.entity.OrderItem;
import com.ejemplo.PruebaAPISpringBoot.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final ProductService productService;

    public OrderService(OrderRepository orderRepository, ProductService productService) {
        this.orderRepository = orderRepository;
        this.productService = productService;
    }

    @Transactional
    public Order createOrder(OrderRequestDto request) {
        Order order = new Order();
        order.setClientName(request.getClientName());
        order.setStatus("CREATED");
        order.setCreatedAt(Instant.now());

        List<OrderItem> items = request.getItems().stream().map(i -> {
            OrderItem it = new OrderItem();
            it.setProductId(i.getProductId());
            int qty = i.getQuantity() == null ? 1 : i.getQuantity();
            it.setQuantity(qty);

            // Uso del helper bloqueante seguro: getProductByIdBlocking
            try {
                Optional<ProductDto> prodOpt = productService.getProductByIdBlocking(i.getProductId());
                if (prodOpt.isPresent()) {
                    ProductDto p = prodOpt.get();
                    it.setTitle(p.getTitle());
                    it.setPrice(p.getPrice());
                } else {
                    it.setTitle("Product unknown " + i.getProductId());
                    it.setPrice(0.0);
                }
            } catch (Exception ex) {
                log.warn("Error fetching product {}: {}, saving fallback values", i.getProductId(), ex.toString());
                it.setTitle("Product unknown " + i.getProductId());
                it.setPrice(0.0);
            }

            return it;
        }).collect(Collectors.toList());

        double total = items.stream()
                .mapToDouble(it -> (it.getPrice() == null ? 0.0 : it.getPrice()) * (it.getQuantity() == null ? 0 : it.getQuantity()))
                .sum();
        order.setItems(items);
        order.setTotal(total);
        return orderRepository.save(order);
    }

    public List<Order> listOrders() {
        return orderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Order> getOrder(Long id) {
        return orderRepository.findByIdWithItems(id);
    }

    @Transactional
    public Order markPaid(Long orderId) {
        var ord = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        ord.setStatus("PAID");
        return orderRepository.save(ord);
    }
}