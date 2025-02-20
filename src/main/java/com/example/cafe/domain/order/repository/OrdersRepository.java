package com.example.cafe.domain.order.repository;

import com.example.cafe.domain.order.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
    Optional<Orders> findByOrderId(Long orderId);
    Optional<Orders> findByEmail(String email);
}
