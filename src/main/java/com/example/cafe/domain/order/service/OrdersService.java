package com.example.cafe.domain.order.service;

import com.example.cafe.domain.order.entity.Orders;
import com.example.cafe.domain.order.entity.OrdersItem;
import com.example.cafe.domain.order.repository.OrdersRepository;
import com.example.cafe.domain.product.entity.Product;
import com.example.cafe.domain.product.repository.ProductRepository;
import com.example.cafe.domain.product.service.ProductService;
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
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

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

    // 단일 상품 주문 담기
    public Orders orderProduct(String productName, int quantity, String email, String address, int postCode) {

        // 상품명으로 Product 정보를 조회하여 변수에 저장
        Product product = productService.findByName(productName);

        OrdersItem orderedItem = createOrderItem(product, quantity);

        return createOrder(product, quantity, email, address, postCode);
    }

    // 단일 상품 주문 생성
    private Orders createOrder(Product product, int quantity, String email, String address, int postCode) {

        // Order 객체를 생성해 회원 이메일, 주소, 우편번호 객체 생성 (나중에 날짜도 생성)
        Orders orders = Orders.builder()
                .email(email)
                .address(address)
                .postCode(postCode)
                .build();

        // Orders 객체 영속화 후 리턴
        return ordersRepository.save(orders);  // 해당 변수는 로컬(자바 메모리)에 저장된 값은 반환됨
    }

    private OrdersItem createOrderItem(Orders orders, Product product, int quantity) {
        // 구매한 상품 객체 생성
        return OrdersItem.builder()
                .orders(orders)
                .orderProductId(product.getProductId())
                .orderProductName(product.getName())
                .orderProductPrice(product.getPrice())
                .quantity(quantity)
                .build();
    }
    
    // 다중 상품 주문 담기
    public Orders orderProducts(ArrayList<String> productNames, String email, String address, int postCode) {

        // 주문자 정보부터 변수에 담기
        ArrayList<Product> products = new ArrayList<>();

        for (String productName : productNames) {

            // 상품명으로 상품 찾기
            Product orderProduct = productRepository.findByName(productName).get();

            // 상품 담기
            products.add(orderProduct);
        }

        return createOrders(products, email, address, postCode);
    }

    // 다중 상품 주문 생성
    private Orders createOrders(ArrayList<Product> products, String email, String address, int postCode) {

        // Order 객체를 생성해 회원 이메일, 주소, 우편번호 객체 생성 (나중에 날짜도 생성)
        Orders orders = Orders.builder()
                .email(email)
                .address(address)
                .postCode(postCode)
                .build();

        // 구매한 상품 객체를 ArrayList 타입인 Product에 저장
        for (Product product : products) {
            OrdersItem ordersItem = OrdersItem.builder()
                    .orderProductId(product.getProductId())
                    .orderProductName(product.getName())
                    .orderProductPrice(product.getPrice())
                    .quantity(1)    // 수량 1 추가 (임시)
                    .build();

            orders.addOrdersItem(ordersItem);
        }

        // Orders 객체 영속화 후 리턴
        return ordersRepository.save(orders);
    }
}
