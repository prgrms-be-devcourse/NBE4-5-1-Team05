package com.example.cafe.domain.order.service;

import com.example.cafe.domain.order.entity.Orders;
import com.example.cafe.domain.order.repository.OrdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrdersService {

    private final OrdersRepository ordersRepository;
    
    // 주문 id 찾기
    public Optional<Orders> findById(Long id) {
        return ordersRepository.findById(id);
    }

    // 주문 정보 기입
    public Orders add(String email, String address, int postCode) {

        Orders orders = Orders.builder()
                .email(email)
                .address(address)
                .postCode(postCode)
                .build();

        return ordersRepository.save(orders);
    }
}
