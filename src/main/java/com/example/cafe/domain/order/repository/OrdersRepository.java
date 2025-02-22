package com.example.cafe.domain.order.repository;

import com.example.cafe.domain.order.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrdersRepository extends JpaRepository<Orders, Long> {

    // 찾기
    Optional<Orders> findByOrderId(Long orderId);
    Optional<Orders> findByEmail(String email);
    Optional<Orders> findByAddress(String address);
    List<Orders> findAll();

    // 삭제
    void deleteByOrderId(Long orderId);
    void deleteByEmail(String email);
    void deleteByAddress(String address);

    // 갯수 세기
    long count();
}
