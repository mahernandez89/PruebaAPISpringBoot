package com.ejemplo.PruebaAPISpringBoot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ejemplo.PruebaAPISpringBoot.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
