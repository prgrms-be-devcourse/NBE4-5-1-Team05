package com.example.cafe;

import com.example.cafe.domain.order.service.OrdersService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@EnableJpaAuditing
@ActiveProfiles("test")
class CafeApplicationTests {

	@Autowired
	private OrdersService ordersService;

	//출력 시험
	@Test
	void contextLoads() {
		System.out.println("Hello World");
	}

	// 단일 상품 주문
	@Test
	void orderProduct() {
		
	}

	// 다중 상품 주문
	@Test
	void ordersProducts() {

	}

}
