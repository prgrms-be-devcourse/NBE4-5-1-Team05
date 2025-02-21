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
        productService.add("아메리카노", 4500, "이미지");
        productService.add("카페라떼", 5500, "이미지");
        productService.add("아이스티", 3500, "이미지");

    }

    @Transactional
    public void orderInit() {

        // 샘플 데이터 생성 (유저 정보 작성)
        Orders o1 = ordersService.add("haeun9988@naver.com", "서울시 구로구", 352);
        Orders o2 = ordersService.add( "apple1234@google.com", "서울시 강남구", 12345);
        Orders o3 = ordersService.add( "banana9876@google.com", "경기도 고양시", 86452);

        // 샘플 데이터 생성 (주문서 작성)
        ordersItemService.orderProduct(o1,"아메리카노", 1);
        ordersItemService.orderProduct(o2,"아메리카노", 2);
        ordersItemService.orderProduct(o3, "카페라떼", 1);
    }

}
