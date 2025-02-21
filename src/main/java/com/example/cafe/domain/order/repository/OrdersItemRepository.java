package com.example.cafe.domain.order.repository;

import com.example.cafe.domain.order.entity.Orders;
import com.example.cafe.domain.order.entity.OrdersItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrdersItemRepository extends JpaRepository<OrdersItem, Long> {
    Optional<OrdersItem> findByOrdersItemId(Long orderId);
    Optional<OrdersItem> findByOrders(Orders orders);
}
