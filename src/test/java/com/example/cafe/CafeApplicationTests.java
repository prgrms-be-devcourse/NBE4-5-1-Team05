package com.example.cafe;

import com.example.cafe.domain.product.entity.Product;
import com.example.cafe.domain.product.repository.ProductRepository;
import com.example.cafe.domain.product.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@EnableJpaAuditing
@ActiveProfiles("test")
class CafeApplicationTests {

	@Autowired
	private ProductService productService;

	@Autowired
	private ProductRepository productRepository;

	@Test
	@DisplayName("출력 시험")
	void contextLoads() {
		System.out.println("Hello World");
	}

	@Test
	@DisplayName("단일 상품 주문")
	void orderProduct() {

		// product 객체 데이터를 어디에서 받을것인가? 어떠한 레이어에서 넘길것인가? ->서비스 -> repository.saver(product)
		// Controller에서 param 값이 오겠죠 -> 메서드 호출
		// Service 로직에서 parameter 들로 빌더하고 영속화(repo)작업
		// 1. builder() service.메서드()안에 넣기
		// 2. 영속화해서 가져와 보기
		//	- DB에 들려서 findbyid findbyentity 값 가져와서 비교해보기

		// 상품 이름만 넘겨 변수에 저장 (아메리카노 한잔 주문)
		Optional<Product> productBuy = productService.orderProduct("아메리카노");

		// 영속화해서 id로 찾아 변수에 저장
		Product foundProduct = productRepository.findById(productBuy.get().getId()).orElse(null);

		// 검증
		assertThat(foundProduct.getId()).isEqualTo(1L);
		assertThat(foundProduct.getName()).isEqualTo("아메리카노");
		assertThat(foundProduct.getPrice()).isEqualTo(4500);
	}

	@Test
	@DisplayName("다중 상품 주문")
	void ordersProducts() {

	}

}
