package com.example.cafe;

import com.example.cafe.domain.order.entity.Orders;
import com.example.cafe.domain.order.repository.OrdersRepository;
import com.example.cafe.domain.order.service.OrdersService;
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
    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    private OrdersService ordersService;

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

//		// 상품 이름만 넘겨 변수에 저장 (아메리카노 한잔 주문)
//		Optional<Product> productBuy = productService.orderProduct("아메리카노");
//
//		// 영속화해서 id로 찾아 변수에 저장
//		Product foundProduct = productRepository.findById(productBuy.get().getId()).orElse(null);
//
//		// 검증
//		assertThat(foundProduct.getId()).isEqualTo(1L);
//		assertThat(foundProduct.getName()).isEqualTo("아메리카노");
//		assertThat(foundProduct.getPrice()).isEqualTo(4500);

		// 주문할 상품명, 이메일, 주소, 우편주소 하나 저장
		String productName = "아메리카노";
		String email = "test1@gmail.com";
		String address = "테스트용 주소1";
		int postCode = 12345;


		// 상품을 주문
		Optional<Orders> orderProduct = ordersService.orderProduct(productName, email, address, postCode);

		// 영속화 후 id로 찾아 변수에 저장
		Optional<Orders> orders = ordersRepository.findById(orderProduct.get().getOrderId());

		// 검증- 반환된 주문 아이디가 2인지 확인
		assertThat(orders.get().getOrderId()).isEqualTo(2L);
		// 검증 - 반환된 상품명이 아메리카노인지 확인

		// 검증 - 반환된 이메일이 맞는지 확인
		assertThat(orders.get().getEmail()).isEqualTo(email);
		// 검증 - 반환된 주소가 맞는지 확인
		assertThat(orders.get().getAddress()).isEqualTo(address);
		// 검증 - 반환된 우편번호가 맞는지 확인
		assertThat(orders.get().getPostCode()).isEqualTo(postCode);
	}

//	@Test
//	@DisplayName("다중 상품 주문")
//	void ordersProducts() {
//		// 주문할 상품 이름 2개 저장
//		List<String> productNames = Arrays.asList("아메리카노", "카페라떼");
//
//		// 상품 이름들만 넘겨 변수에 저장
//		List<Product> productsBuy = productService.ordersProducts(productNames);
//
//		// 영속화해서 id로 찾아 변수에 저장
//		List<Product> foundsProduct = productRepository.findById(productsBuy.get().getId()).orElse(null);
//
//		// 검증(크기가 2인지 확인)
//		assertThat(foundsProduct).hasSameClassAs(2);
//	}

}
