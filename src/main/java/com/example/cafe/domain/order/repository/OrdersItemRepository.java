package com.example.cafe.domain.order.repository;

import com.example.cafe.domain.order.entity.OrdersItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersItemRepository extends JpaRepository<OrdersItem, Long> {
}
