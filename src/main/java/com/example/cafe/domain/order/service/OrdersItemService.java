package com.example.cafe.domain.order.service;

import com.example.cafe.domain.order.entity.Orders;
import com.example.cafe.domain.order.entity.OrdersItem;
import com.example.cafe.domain.order.repository.OrdersItemRepository;
import com.example.cafe.domain.order.repository.OrdersRepository;
import com.example.cafe.domain.product.entity.Product;
import com.example.cafe.domain.product.service.ProductService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@Builder
@RequiredArgsConstructor
public class OrdersItemService {

    @Autowired
    private final OrdersItemRepository ordersItemRepository;

    @Autowired
    private final ProductService productService;

    @Autowired
    private final OrdersRepository ordersRepository;

    // 주문 id로 주문 내역 찾기
    public Optional<OrdersItem> findById(Long orderId) {
        return ordersItemRepository.findById(orderId);
    }

    // 이메일로 주문 내역 찾기
    public OrdersItem findOrderByEmail(String email) {

        return ordersItemRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("조회하려는 이메일이 없습니다."));
    }

    // TODO: orderRepository save가 다중 작업시 반복처리 되는것이 신경쓰인다. jpa가 효율적으로 해주나?
    // TODO: 다중 상품 주문 담기에서 quantity 로직 매커니즘이 올바르지 않다.
    // TODO: Repository 호출이 최소화, 저는 빈번해요 -> jpa 기능 중에 일괄처리 작동 이것이  지혼자 해주는지? 그게 기억이안나요 -> transactional 처리를 해야되요

    // 상품 주문 담기
    public void orderProducts(Orders orders, ArrayList<String> productNames, ArrayList<Integer> quantity) {
        //OrdersItem 수량이 의미하는
        /*
         *각각의 Product에 해당하는 수량이 필요할것 같아요
         * */

//      // 들어있는 상품명의 갯수에 따라 단일로 저장
//        for (String productName : productNames) {
//            orderProduct(orders, productName, quantity);
//        }

        // 들어있는 상품명의 갯수에 따라 단일로 저장
        for (int i = 0; i < productNames.size(); i++) {
            orderProduct(orders, productNames.get(i), quantity.get(i));
        }

//        ordersItemRepository.saveAll(ordersItems);

//        return ordersRepository.save(orders);
    }

    // 단일 상품 주문 담기
    public void orderProduct(Orders orders, String productName, int quantity) {

        // 상품명으로 Product 정보를 조회하여 변수에 저장
        Product product = productService.findByName(productName);

        // createOrderItem 메서드에 구매자 정보, 상품, 수량을 보내 객체 생성 후 변수에 저장
        OrdersItem orderedItem = createOrderItem(orders, product, quantity);
        orders.addOrdersItem(orderedItem);

        // ordersItemRepository 레포지터리에 저장
        ordersItemRepository.save(orderedItem);
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
}
