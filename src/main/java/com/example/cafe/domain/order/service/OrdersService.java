package com.example.cafe.domain.order.service;

import com.example.cafe.domain.order.entity.Orders;
import com.example.cafe.domain.order.repository.OrdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrdersService {

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private OrdersItemService ordersItemService;

    // 구매자 id로 구매자 찾기
    public Optional<Orders> findByOrderId(Long orderId) {
        return ordersRepository.findById(orderId);
    }

    // 구매자 이메일로 구매자 찾기
    public Orders findOrderByEmail(String email) {
        return ordersRepository.findByOrderEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("조회하려는 이메일이 없습니다."));
    }

    // 구매자 주소로 구매자 찾기
    public Optional<Orders> findByAddress(String address) {
        return ordersRepository.findByOrderAddress(address);
    }

    // 모든 구매자 찾기
    public List<Orders> findAll() {
        return ordersRepository.findAll();
    }

    // 주문자 id로 삭제
    boolean deleteByOrderId(Long orderId) {
        if (!ordersRepository.existsById(orderId)) {
            return false;
        }
        ordersRepository.deleteById(orderId);

        return true;
    }

    // 주문자 이메일로 삭제
    boolean deleteByEmail(String email) {
        if (ordersRepository.findByOrderEmail(email).isEmpty()) {
            return false;
        }
        ordersRepository.deleteByEmail(email);

        return true;
    }

    // 주문자 주소로 삭제
    boolean deleteByAddress(String address) {
        if (ordersRepository.findByOrderAddress(address).isEmpty()) {
            return false;
        }
        ordersRepository.deleteByAddress(address);

        return true;
    }
    
    // 수정

    // 주문자 정보 기입
    public Orders add(String email, String address, int postCode) {

        Orders orders = Orders.builder()
                .email(email)
                .address(address)
                .postCode(postCode)
                .build();

        return ordersRepository.save(orders);
    }

    // 주문서 작성하기 (시작)
    public void startOrders(ArrayList<String> productName, ArrayList<Integer> quantity, String email, String address, int postCode) {

        // Orders에 구매자 저장
        Orders orders = add(email, address, postCode);

        // orderProducts로 구매내역 저장
        ordersItemService.orderProducts(orders, productName, quantity);
    }
}
