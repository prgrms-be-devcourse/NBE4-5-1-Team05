package com.example.cafe.domain.order.service;

import com.example.cafe.domain.order.entity.Order;
import com.example.cafe.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    
    // 주문 id 찾기
    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    // 주문 정보 기입
    public Order add(String email, String address, int postCode) {

        Order order = Order.builder()
                .email(email)
                .address(address)
                .postCode(postCode)
                .build();

        return orderRepository.save(order);
    }
}
