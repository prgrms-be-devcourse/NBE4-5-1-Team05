package com.example.cafe.domain.order.service;

import com.example.cafe.domain.order.entity.Orders;
import com.example.cafe.domain.order.repository.OrdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrdersService {

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private OrdersItemService ordersItemService;

    // 주문자 정보 기입
    public Orders add(String email, String address, int postCode) {

        Orders orders = Orders.builder()
                .email(email)
                .address(address)
                .postCode(postCode)
                .build();

        return ordersRepository.save(orders);
    }

    // 구매자 id로 구매자 찾기
    public Optional<Orders> findById(Long orderId) {
        return ordersRepository.findById(orderId);
    }

    // 구매자 이메일로 구매자 찾기
    public Orders findOrderByEmail(String email) {

        return ordersRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("조회하려는 이메일이 없습니다."));
    }

    // 주문서 작성하기 (시작)
    public void startOrders(ArrayList<String> productName, ArrayList<Integer> quantity, String email, String address, int postCode) {

        // Orders에 구매자 저장
        Orders orders = add(email, address, postCode);

        // orderProducts로 구매내역 저장
        ordersItemService.orderProducts(orders, productName, quantity);
    }
}
