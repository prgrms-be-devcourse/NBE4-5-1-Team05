package com.example.cafe.domain.order.service;

import com.example.cafe.domain.order.entity.Orders;
import com.example.cafe.domain.order.entity.OrdersItem;
import com.example.cafe.domain.order.repository.OrdersRepository;
import com.example.cafe.domain.product.entity.Product;
import com.example.cafe.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrdersService {

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private ProductRepository productRepository;

    // 주문자 정보 기입
    public Orders add(String email, String address, int postCode) {

        Orders orders = Orders.builder()
                .email(email)
                .address(address)
                .postCode(postCode)
                .build();

        return ordersRepository.save(orders);
    }
    
    // 주문 id로 주문 내역 찾기
    public Optional<Orders> findById(Long id) {
        return ordersRepository.findById(id);
    }

    // 이메일로 주문 내역 찾기
    public Orders findOrderByEmail(String email) {

        return ordersRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("조회하려는 이메일이 없습니다."));
    }

    // 상품 담기
    public Orders orderProduct(String productName, String email, String address, int postCode) {

        // 상품명으로 Product 정보를 조회하여 변수에 저장
        Optional<Product> ordersProduct = productRepository.findByName(productName);

        Product product = ordersProduct.get();

        return createOrder(product, email, address, postCode);
    }

    // 주문 생성
    private Orders createOrder(Product product, String email, String address, int postCode) {

        // Order 객체를 생성해 회원 이메일, 주소, 우편번호 객체 생성 (나중에 Date도 생성)
        Orders orders = Orders.builder()
                .email(email)
                .address(address)
                .postCode(postCode)
                .build();

        // 구매한 상품 객체 생성
        OrdersItem ordersItem = OrdersItem.builder()
                .orderProductId(product.getProductId())
                .orderProductName(product.getName())
                .orderProductPrice(product.getPrice())
                .quantity(1)    // 수량 1 추가 (임시)
                .build();

        // 구매한 상품 추가
        orders.addOrdersItem(ordersItem);

        // Orders 객체 영속화
        return ordersRepository.save(orders);  // 해당 변수는 로컬(자바 메모리)에 저장된 값은 반환됨
    }
}
