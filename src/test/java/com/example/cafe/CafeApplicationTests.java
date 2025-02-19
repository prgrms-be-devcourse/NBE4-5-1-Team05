package com.example.cafe;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@EnableJpaAuditing
@ActiveProfiles("test")
class CafeApplicationTests {

	//출력 시험
	@Test
	void contextLoads() {
		System.out.println("Hello World");
	}

	// 단일 상품 주문 후 출력
	@Test
	void orderAndPrint() {
		
	}

	// 다중 상품 주문 후 출력
	@Test
	void ordersAndPrint() {

	}

}
