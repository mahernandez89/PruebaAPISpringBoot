package com.ejemplo.PruebaAPISpringBoot.service;

import com.ejemplo.PruebaAPISpringBoot.dto.OrderRequestDto;
import com.ejemplo.PruebaAPISpringBoot.dto.ProductDto;
import com.ejemplo.PruebaAPISpringBoot.entity.Order;
import com.ejemplo.PruebaAPISpringBoot.entity.OrderItem;
import com.ejemplo.PruebaAPISpringBoot.exception.OrderNotFoundException;
import com.ejemplo.PruebaAPISpringBoot.mapper.OrderMapper;
import com.ejemplo.PruebaAPISpringBoot.repository.OrderRepository;
import com.ejemplo.PruebaAPISpringBoot.service.client.ProductBlockingAdapter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {
    //private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final ProductBlockingAdapter productBlockingAdapter;
    private final OrderMapper orderMapper;

    public OrderService(OrderRepository orderRepository,
                        ProductBlockingAdapter productBlockingAdapter,
                        OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.productBlockingAdapter = productBlockingAdapter;
        this.orderMapper = orderMapper;
    }

    @Transactional
    public Order createOrder(OrderRequestDto request) {
        if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }
        Order order = orderMapper.toEntity(request);
        List<Integer> productIds = order.getItems().stream()
                .map(OrderItem::getProductId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Map<Integer, ProductDto> productInfo = Collections.emptyMap();
        if (!productIds.isEmpty()) {
            productInfo = productBlockingAdapter.fetchByIdsBlocking(productIds);
        }
        for (OrderItem it : order.getItems()) {
            ProductDto p = productInfo.get(it.getProductId());
            if (p != null) {
                it.setTitle(p.getTitle());
                it.setPrice(p.getPrice());
            } else {
                it.setTitle("Product unknown " + it.getProductId());
                it.setPrice(0.0);
            }
        }

        double total = order.getItems().stream()
                .mapToDouble(it -> (it.getPrice() == null ? 0.0 : it.getPrice()) * (it.getQuantity() == null ? 0 : it.getQuantity()))
                .sum();
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
        var ord = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
        ord.setStatus("PAID");
        return orderRepository.save(ord);
    }
}