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
    public Optional<Orders> findById(Long orderId) {
        return ordersRepository.findById(orderId);
    }

    // 이메일로 주문 내역 찾기
    public Orders findOrderByEmail(String email) {

        return ordersRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("조회하려는 이메일이 없습니다."));
    }
    
    // 구매자 정보와 구매한 상품들을 객체화시켜 orderProduct로 리턴
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

    // TODO: orderRepository save가 다중 작업시 반복처리 되는것이 신경쓰인다. jpa가 효율적으로 해주나?
    // TODO: 다중 상품 주문 담기에서 quantity 로직 매커니즘이 올바르지 않다.
    // TODO: Repository 호출이 최소화, 저는 빈번해요 -> jpa 기능 중에 일괄처리 작동 이것이  지혼자 해주는지? 그게 기억이안나요 -> transactional 처리를 해야되요

    // 단일로 상품 주문 담기
    public Orders orderProduct(Orders orders, String productName, int quantity) {

        // 상품명으로 Product 정보를 조회하여 변수에 저장
        Product product = productService.findByName(productName);
        OrdersItem orderedItem = createOrderItem(orders, product, quantity);

        orders.addOrdersItem(orderedItem);

        // 레포지터리에 저장 및
        return ordersRepository.save(orders);
    }

    // 다중 상품 주문 담기
    public Orders orderProducts(Orders orders, ArrayList<String> productNames, ArrayList<Integer> quantity) {
       //OrdersItem 수량이 의미하는
        /*
        *각각의 Product에 해당하는 수량이 필요할것 같아요
        * */

       for (String productName : productNames) {
            orderProduct(orders, productName, quantity);
        }

        return ordersRepository.save(orders);
    }
}
