package com.example.cafe.global;

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

    }

    @Transactional
    public void orderInit() {

        // 샘플 데이터 생성 (유저 정보 작성)
        ordersService.orderProduct("아메리카노", 1, "haeun9988@naver.com", "서울시 구로구", 352);
        ordersService.orderProduct("아메리카노", 2, "apple1234@google.com", "서울시 강남구", 12345);
        ordersService.orderProduct("카페라떼", 1, "banana9876@google.com", "경기도 고양시", 86452);
    }

}
