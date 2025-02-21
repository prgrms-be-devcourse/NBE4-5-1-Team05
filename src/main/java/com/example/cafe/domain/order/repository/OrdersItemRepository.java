package com.example.cafe.domain.order.repository;

import com.example.cafe.domain.order.entity.Orders;
import com.example.cafe.domain.order.entity.OrdersItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrdersItemRepository extends JpaRepository<OrdersItem, Long> {

    // 찾기
    Optional<OrdersItem> findByOrdersItemId(Long orderId);
    Optional<OrdersItem> findByOrders(Orders orders);
    Optional<OrdersItem> findOrdersItemByOrdersEmail(String ordersEmail);
    Optional<OrdersItem> findOrdersItemByOrderDate(LocalDateTime orderDate);
    List<OrdersItem> findAll();

    // 삭제
    boolean deleteOrdersItemByOrdersItemId(Long orderItemId);
    boolean deleteByOrders(Orders orders);
    boolean deleteByOrdersItemByEmail(String email);
    boolean deleteByOrderDate(LocalDateTime orderDate);

    // 수정
    Optional<OrdersItem> modifyOrdersItem(OrdersItem ordersItem);
    Optional<OrdersItem> modifyOrdersItemByOrdersItemId(Long ordersItemId, Orders orders);
    Optional<OrdersItem> modifyOrdersItemByEmail(String email, Orders orders);

    // 갯수 세기
    int countAllOrdersItem();
}
