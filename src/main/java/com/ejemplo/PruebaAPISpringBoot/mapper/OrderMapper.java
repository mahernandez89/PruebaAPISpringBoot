package com.ejemplo.PruebaAPISpringBoot.mapper;

import com.ejemplo.PruebaAPISpringBoot.dto.OrderRequestDto;
import com.ejemplo.PruebaAPISpringBoot.entity.Order;
import com.ejemplo.PruebaAPISpringBoot.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {
        public Order toEntity(OrderRequestDto request) {
        Order order = new Order();
        order.setClientName(request.getClientName());
        order.setStatus("CREATED");
        order.setCreatedAt(Instant.now());
        List<OrderItem> items = request.getItems().stream().map(i -> {
            OrderItem it = new OrderItem();
            it.setProductId(i.getProductId());
            it.setQuantity(i.getQuantity() == null ? 1 : i.getQuantity());
            return it;
        }).collect(Collectors.toList());
        order.setItems(items);
        return order;
    }
}
