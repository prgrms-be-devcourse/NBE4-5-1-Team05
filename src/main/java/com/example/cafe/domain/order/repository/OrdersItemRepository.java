package com.example.cafe.domain.order.repository;

import com.example.cafe.domain.order.entity.Orders;
import com.example.cafe.domain.order.entity.OrdersItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrdersItemRepository extends JpaRepository<OrdersItem, Long> {

    // 찾기
    OrdersItem findByOrdersItemId(Long orderId);
    List<OrdersItem> findOrdersItemByOrdersEmail(String ordersEmail);
    Optional<OrdersItem> findOrdersItemByOrderDate(LocalDateTime orderDate);
    Optional<OrdersItem> findOrdersItemByCompleted(boolean completed);
    List<OrdersItem> findAll();

    // 삭제
    void deleteOrdersItemByOrdersItemId(Long orderItemId);
    void deleteByOrders(Orders orders);
    void deleteByOrderDate(LocalDateTime orderDate);

    // 갯수 세기
    long count();
}
