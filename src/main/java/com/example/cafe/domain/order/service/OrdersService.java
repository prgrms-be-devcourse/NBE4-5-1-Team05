package com.example.cafe.domain.order.service;

import com.example.cafe.domain.order.entity.Orders;
import com.example.cafe.domain.order.entity.OrdersItem;
import com.example.cafe.domain.order.repository.OrdersRepository;
import com.example.cafe.domain.product.entity.Product;
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

    // 주문
    public Orders createOrder(Product product, String email, String address, int postCode) {

        // Order 객체를 생성해 회원 이메일, 주소, 우편번호 객체 생성 (나중에 Date도 생성)
        Orders orders = Orders.builder()
                .email(email)
                .address(address)
                .postCode(postCode)
                .build();

        // 구매한 상품 객체 생성
        OrdersItem ordersItem = OrdersItem.builder()
                .product(product)
                .quantity(1)    // 수량 1 추가 (임시)
                .price(product.getPrice())
                .build();

        // 구매한 상품 추가
        orders.addOrdersItem(ordersItem);

        // Orders 객체 영속화
        ordersRepository.save(orders);

        return orders;
    }
}
