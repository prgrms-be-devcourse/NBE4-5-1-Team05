package com.example.cafe;

import com.example.cafe.domain.order.entity.Orders;
import com.example.cafe.domain.order.entity.OrdersItem;
import com.example.cafe.domain.order.repository.OrdersRepository;
import com.example.cafe.domain.order.service.OrdersService;
import com.example.cafe.domain.product.entity.Product;
import com.example.cafe.domain.product.repository.ProductRepository;
import com.example.cafe.domain.product.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@EnableJpaAuditing
@ActiveProfiles("test")
@Transactional
@Configuration
class CafeApplicationTests {

	@Autowired
	private ProductService productService;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private OrdersRepository ordersRepository;

	@Autowired
	private OrdersService ordersService;

//	// 테스트 전 한번만 실행
//	@BeforeAll
//	static void beforeAll() {
//		// db 저장 작업까지하시고
//		// before each product들만 넣으시고
//		// test case로 작업하는게 좋아보여요
//		// 목적) 프로젝트 시작과 더불어 가장 처음 product data 세팅하자!
//		// - db에 저장된 product 정보를 가져온다.
//		// - baseInitdata 여기서
//		// - cafeApplication main() 여기서
//		// - test case- beforeall
//		// 어차피 baseinitdata에서 data 세팅을하면 test케이스도 적용이 된다. 선택이다.
//		// beforeAll - product 만 준비하고
//		//             커피, 바나나 ,사과, 에 대한 정보
//		// test case에서 주문을 준비한다.
//		// 실제 주문하는 상황을 가정해서 생각해야 설계가 이후에 별로 안바
//		// 데이터로 그냥 집어넣고 시작하면
//		// 사용자가 넘겨주는 데이터의 흐름 생각하지 않게 x
//		// 이미 값이 들어가
//		// ex orders랑 ordersItem
//		// 사실은 상호의존적관계 코드상 둘중에 하나만 있을 수가 없게되어있어
//		// test케이스 수기작성하기
//		// 확인 하는 절차는 test case에서
//
//		// 다중 주문하였을때, 한명이 하나의 orders내에서 여러개의 ordersitem
//
//		// 한명이 두개의 orders 생기는 경우
//		// 이것을 어떻게 처리할 것인지?
//		// - orders 기존 추가할까?
//		// - 아니야 두개는 다른거야
//		// 1. 날짜 비교 통해서 나중에 일괄 처리하여 배송이 완료됨을 알린다. ()
//	}

	@Test
	@DisplayName("출력 시험")
	void contextLoads() {
		System.out.println("Hello World");
	}

	@Test
	@DisplayName("모든 상품 조회")
	void findAllProducts() {

		List<Product> products = productRepository.findAll();

		assertThat(products).isNotNull();
		assertThat(products.size()).isEqualTo(2);

	}

	@Test
	@DisplayName("저장되어 있는 모든 구매내역 조회")
	void findAllOrders() {

		List<Orders> ordersList = ordersRepository.findAll();

		assertThat(ordersList).isNotNull();
		assertThat(ordersList.size()).isGreaterThan(0);
	}

	@Test
	@DisplayName("단일 상품 주문")
	void orderProduct() {

		// 주문할 상품명, 이메일, 주소, 우편주소 하나 저장
		String productName = "카페라떼";
		String email = "test2@gmail.com";
		String address = "테스트용 주소2";
		int postCode = 12345;

		// 상품을 주문
		Orders orderProduct = ordersService.orderProduct(productName, email, address, postCode);

		// 영속화 후 id로 찾아 변수에 저장
		Orders orders = ordersRepository.findById(orderProduct.getOrderId()).orElse(null);

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
        List<OrdersItem> ordersItems = orders.getOrdersItems();
		OrdersItem savedOrdersItem = ordersItems.getFirst();

		assertThat(savedOrdersItem.getOrderProductName()).isEqualTo(productName);
	}

	@Test
	@DisplayName("이메일로 단일 주문 내역 찾기 - 성공")
	void findByEmailSuccess() {
		// 주문할 상품명, 이메일, 주소, 우편주소 하나 저장
		String productName = "아메리카노";
		String email = "test3@gmail.com";
		String address = "테스트용 주소3";
		int postCode = 54867;

		// 상품을 주문
		Orders orderProduct = ordersService.orderProduct(productName, email, address, postCode);

		// 영속화
		ordersRepository.findById(orderProduct.getOrderId()).orElse(null);

		// 저장해 둔 이메일
		String email2 = "test3@gmail.com";

		// 이메일로 주문내역 찾아 변수에 저장
		Orders foundOrder = ordersService.findOrderByEmail(email2);

		// 조회 된 주문이 비어있는지 확인
		assertThat(foundOrder).isNotNull();
		
		//조회 된 이메일이 똑같은지 확인
		assertThat(foundOrder.getEmail()).isEqualTo(email2);

		// 검증 - 반환된 상품명이 아메리카노인지 확인
		List<OrdersItem> ordersItems = foundOrder.getOrdersItems();
		OrdersItem savedOrdersItem = ordersItems.getFirst();

		assertThat(savedOrdersItem.getOrderProductName()).isEqualTo(productName);
	}

	@Test
	@DisplayName("이메일로 단일 주문 내역 찾기 - 실패")
	void findByEmailFail() {

		// 저장해 둔 이메일
		String email = "testX@gmail.com";

		// 조회 된 주문이 비어있는지 확인
		assertThatThrownBy(() -> {
			ordersService.findOrderByEmail(email);
		})
				.isInstanceOf(RuntimeException.class)
				.hasMessage("조회하려는 이메일이 없습니다.");
	}

	@Test
	@DisplayName("다중 상품 주문 + 이메일로 찾아 검증")
	void ordersProducts() {

		// 주문할 상품 이름 2개, 이메일, 주소, 우편번호 저장
		ArrayList<String> productNames = new ArrayList<>();
			productNames.add("아메리카노");
			productNames.add("카페라떼");
		String email = "test4@gmail.com";
		String address = "테스트용 주소4";
		int postCode = 78645;

		// 상품 이름들만 넘겨 변수에 저장
		Orders multiOrders = ordersService.orderProducts(productNames, email, address, postCode);

		// 영속화
		Orders orders = ordersRepository.save(multiOrders);

		// 이메일로 찾아 변수에 저장
		Orders foundProduct = ordersService.findOrderByEmail(email);

		/// 검증 ///
		// 이메일
		assertThat(foundProduct.getEmail()).isEqualTo(email);

		// 주소
		assertThat(foundProduct.getAddress()).isEqualTo(address);

		// 우편번호
		assertThat(foundProduct.getPostCode()).isEqualTo(postCode);

		// 주문 음료 이름
		ArrayList<OrdersItem> OrdersItems = new ArrayList<>(orders.getOrdersItems());
		ArrayList<String> OrdersProductNames = OrdersItems.stream()
				.map(OrdersItem::getOrderProductName)
				.collect(Collectors.toCollection(ArrayList::new));

		assertThat(OrdersProductNames).contains("아메리카노");
		assertThat(OrdersProductNames).contains("카페라떼");
	}

//	@Test
//	@DisplayName("단일로 주문한 상품의 수량 확인")
//	void findOrderProductQuantity() {
//
//	}

// 	@Test
//	@DisplayName("다중으로 주문한 각 상품의 수량 확인")
//	void findOrderProductsQuantity() {
//
//	}

//	@Test
//	@DisplayName("오후 2시부터 다음날 오후 2시까지의 한 구매자의 상품 확인")
//	void findOrderProduct2pmBefore() {
//
//	}

//	@Test
//	@DisplayName("오후 2시부터 다음날 오후 2시 이후까지의 한 구매자의 상품 확인")
//	void findOrderProduct2pmAfter() {
//
//	}
}
