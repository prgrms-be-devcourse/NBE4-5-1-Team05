package com.example.cafe.domain.order.repository;

import com.example.cafe.domain.order.entity.OrdersItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrdersItemRepository extends JpaRepository<OrdersItem, Long> {
    Optional<OrdersItem> findByOrderId(Long orderId);
    Optional<OrdersItem> findByEmail(String email);
}
