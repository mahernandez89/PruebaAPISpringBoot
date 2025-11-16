package com.ejemplo.PruebaAPISpringBoot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

import com.ejemplo.PruebaAPISpringBoot.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("select o from Order o left join fetch o.items where o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") Long id);
}
