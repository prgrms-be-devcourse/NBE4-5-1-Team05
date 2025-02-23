package com.example.cafe.domain.order.service;

import com.example.cafe.domain.order.entity.Orders;
import com.example.cafe.domain.order.repository.OrdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /// 기본 메서드 ///
    // 구매자 id로 구매자 찾기
    public Optional<Orders> findByOrderId(Long orderId) {
        return ordersRepository.findById(orderId);
    }

    // 구매자 이메일로 구매자 찾기
    public Optional<Orders> findByEmail(String email) {
        return ordersRepository.findByEmail(email);
    }

    // 구매자 주소로 구매자 찾기
    public Optional<Orders> findByAddress(String address) {
        return ordersRepository.findByAddress(address);
    }

    // 모든 구매자 찾기
    public List<Orders> findAll() {
        return ordersRepository.findAll();
    }

    // 구매자 id로 삭제
    boolean deleteByOrderId(Long orderId) {
        if (!ordersRepository.existsById(orderId)) {
            return false;
        }
        ordersRepository.deleteById(orderId);

        return true;
    }

    // 구매자 이메일을 받아 삭제
    public void deleteByEmail(String email) {
        if (ordersRepository.findByEmail(email).isEmpty()) {
            System.out.println("이메일에 해당하는 구매자가 없습니다.");
        } else {
            ordersRepository.deleteByEmail(email);
        }
    }
    
    // 구매자 이메일을 받아 수정
    public void modifyByEmail(Orders orders, String ordersEmail, String ordersAddress, int ordersPostCode) {

        // 구매자 이메일 수정
        if (ordersEmail != null) {
            orders.setEmail(ordersEmail);
        }

        // 구매자 주소 수정
        if (ordersAddress != null) {
            orders.setAddress(ordersAddress);
        }

        // 구매자 우편주소
        if (ordersPostCode != 0) {
            orders.setPostCode(ordersPostCode);
        }

        ordersRepository.save(orders);
    }

    /// 기능 메서드 ///
    // 구매자 정보 기입
    public Orders add(String email, String address, int postCode) {

        Orders orders = Orders.builder()
                .email(email)
                .address(address)
                .postCode(postCode)
                .build();

        return ordersRepository.save(orders);
    }

    /// 주문서 작성 ///
    // 주문서 작성하기 (시작)
    @Transactional
    public void startOrders(ArrayList<String> productName, ArrayList<Integer> quantity, String email, String address, int postCode) {

        // Orders에 구매자 저장
        Orders orders = add(email, address, postCode);

        // orderProducts로 구매내역 저장
        ordersItemService.orderProducts(orders, productName, quantity);
    }
}
