package com.example.cafe;

import com.example.cafe.domain.order.entity.Orders;
import com.example.cafe.domain.order.entity.OrdersItem;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@EnableJpaAuditing
@ActiveProfiles("test")
@Transactional
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

		String productName = "아메리카노";
		String email = "test1@gmail.com";
		String address = "테스트용 주소1";
		int postCode = 12345;

		// 상품을 주문
		Orders orderProduct = ordersService.orderProduct(productName, email, address, postCode);

		// 영속화 후 id로 찾아 변수에 저장
		/* ordersRepository
		Orders를 저장을해요 (회원정보를 담고 있어요)
		OrdersItem은 주문정보를 담고있어요

		Orders - OrdersItem의 관계 자바 설계가 조금 이상하다
		Orders
		- 고유 id
		- 회원정보
		- List<OrdersItem> = new List<>;
			- List는 언제 값을 가져올것인가?
			- jpa는 repository로 탐색할때 찾는것이 기본입니다. (Lazy 방식)
				- 그럼 찾는 메서드는 언제 실행시키지?
				findOrders(long email);
				addOrdersItem();

		Orders의 특정 지역변수만 알고있을때 OrdersItem을 찾아야 돼요
		요구조건이 충실한거고, 확장성이 고려되지않음 이름으로 찾고
		- Orders o = Orders.findByemail();
		- long id = o.getId();
		- OrdersItem oI = ordersRepository.findbyOrdersId(Orders.getid())
		- 이것을 출력한다.

		- for(OrdersItem oi : Orders)
		{
		if(oi.getName() == 사용자의 입력값)
		return oi;
		}

		OrdersItem
		- Orders 정보
		- Item 정보

		-> testcase2) 이메일을 통해서 단일 상품조회
		*/

		Orders orders = ordersRepository.findById(orderProduct.getOrderId()).orElse(null);

		// 검증- 반환된 주문 아이디가 2인지 확인
		assertThat(orders.getOrderId()).isEqualTo(2L);

		// 검증 - 반환된 이메일이 맞는지 확인
		assertThat(orders.getEmail()).isEqualTo(email);

		// 검증 - 반환된 주소가 맞는지 확인
		assertThat(orders.getAddress()).isEqualTo(address);

		// 검증 - 반환된 우편번호가 맞는지 확인
		assertThat(orders.getPostCode()).isEqualTo(postCode);

		// 주문이 왔을때 해당 주문에 포함된 아메리카노에 대한 정보를 어디에서 가져올 것인가
		// 상품정보는 BaseInitData() 세팅을 할것이다. -> 커피, 바나나, 사과 (DB -> 자바 메모리)
		// db에 잘저장이되었니(저장 Service를 통해서 저장하고 값을)? 세팅 이후에 자바메모리에서 잘 저장이 되니(해당 Entity의 setter를 통해서 확인)?
		// DB에서 자바메모리로 VO(바나나, 사과, 커피) 들을 옮기는 과정에서 자료구조가 있어야 되겠는데?
		// Product들의 정보를 담는 ProductsList가 있으면 되겠다.
			// 수업중 Comment와 Comments 관계에서 사용되는 방법이 있습니다.
			// Product 객체 내부에 List<Product>를 지역변수로 설정하는 방법
		// 검증하려는 목적이 중요
		// 1. db 에 잘저장되어있는지
		// 2. OrdersItem하고 Product 연결하는 메서드를 작성하였는데 그게 잘 작동하는지
		// 검증 - 반환된 상품명이 아메리카노인지 확인
		// OrderItem -> orderProuductName으로 접근 뒤 검증

		// 검증 - 반환된 상품명이 아메리카노인지 확인
		Orders savedOrder = orders;
		List<OrdersItem> ordersItems = savedOrder.getOrdersItems();
		OrdersItem savedOrdersItem = ordersItems.get(0);

		assertThat(savedOrdersItem.getOrderProductName()).isEqualTo(productName);
	}

	@Test
	@DisplayName("이메일로 단일 주문 내역 찾기")
	void findByEmail() {
		
		// 저장해 둔 이메일
		String email = "test1@gmail.com";

		Orders foundOrder = ordersService.findOrderByEmail(email);
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
