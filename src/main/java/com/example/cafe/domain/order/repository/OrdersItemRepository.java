package com.example.cafe.domain.order.repository;

import com.example.cafe.domain.order.entity.Orders;
import com.example.cafe.domain.order.entity.OrdersItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrdersItemRepository extends JpaRepository<OrdersItem, Long> {

    // 찾기
    OrdersItem findByOrdersItemId(Long orderId);
//    Optional<OrdersItem> findByOrders(Orders orders);
    List<OrdersItem> findOrdersItemByOrdersEmail(String ordersEmail);
    List<OrdersItem> findOrdersItemByOrderDate(LocalDateTime orderDate);
    List<OrdersItem> findOrdersItemByCompleted(boolean completed);
    List<OrdersItem> findAll();

    // 삭제
    boolean deleteOrdersItemByOrdersItemId(Long orderItemId);
    boolean deleteByOrders(Orders orders);
    Integer deleteByOrders_Email(String email);
    boolean deleteByOrderDate(LocalDateTime orderDate);

    // 갯수 세기
    long count();
}
