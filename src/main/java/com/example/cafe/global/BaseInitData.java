package com.example.cafe.global;

import com.example.cafe.domain.order.entity.Orders;
import com.example.cafe.domain.order.service.OrdersItemService;
import com.example.cafe.domain.order.service.OrdersService;
import com.example.cafe.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Configuration
@RequiredArgsConstructor
public class BaseInitData {

    private final OrdersService ordersService;
    private final ProductService productService;

    @Autowired
    @Lazy
    private BaseInitData self;
    @Autowired
    private OrdersItemService ordersItemService;

    @Bean
    @Order(1)
    public ApplicationRunner applicationRunner1() {
        return args -> {
            self.productInit();
            self.orderInit();
        };
    }

    @Transactional
    public void productInit() {

        // 샘플 데이터 생성 (상품 담아두기)
        productService.add("에스프레소 디카페인", 20000, "/images/Espresso_Decaf_SW.jpg");
        productService.add("이디오피아 예가체프", 19000, "/images/Ethiopia_Yirgacheff.jpg");
        productService.add("하우스 블렌드", 18000, "/images/House_Blend.jpg");
        productService.add("수마트라 만델링 다크", 25000, "/images/Sumatra_Dark.jpg");

    }

    @Transactional
    public void orderInit() {

        // 샘플 데이터 생성 (유저 정보 작성)
        Orders o1 = ordersService.add("haeun9988@naver.com", "서울시 구로구", 352);
        Orders o2 = ordersService.add("apple1234@google.com", "서울시 강남구", 12345);
        Orders o3 = ordersService.add("banana9876@google.com", "경기도 고양시", 86452);

        // 샘플 데이터 생성 (주문서 작성)
        ordersItemService.orderProduct(o1, "하우스 블렌드", 1);
        ordersItemService.orderProduct(o2, "하우스 블렌드", 2);
        ordersItemService.orderProduct(o3, "에스프레소 디카페인", 1);

        // 샘플 데이터에 시간 주입
        o1.getOrdersItems().get(0).setOrderDate(LocalDate.now().minusDays(1).atStartOfDay());
        o3.getOrdersItems().get(0).setOrderDate(LocalDate.now().minusDays(2).atStartOfDay());

        // 샘플 데이터에 배송 상태 주입
        o1.getOrdersItems().get(0).setCompleted(true);
        o3.getOrdersItems().get(0).setCompleted(true);
    }
}
