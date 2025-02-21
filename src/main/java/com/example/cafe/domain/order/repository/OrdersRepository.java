package com.example.cafe.domain.order.repository;

import com.example.cafe.domain.order.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrdersRepository extends JpaRepository<Orders, Long> {

    // 찾기
    Optional<Orders> findByOrderId(Long orderId);
    Optional<Orders> findByOrderEmail(String email);
    Optional<Orders> findByOrderAddress(String address);
    List<Orders> findAll();

    // 삭제
    boolean deleteByOrderId(Long orderId);
    boolean deleteByEmail(String email);
    boolean deleteByAddress(String address);

    // 수정
    Optional<Orders> modifyByOrderId(Long orderId, Orders orders);
    Optional<Orders> modifyByEmail(String email, Orders orders);
    Optional<Orders> modifyByAddress(String address, Orders orders);

    // 갯수 세기
    int countAllOrders();
}
