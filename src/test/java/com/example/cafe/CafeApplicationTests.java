package com.example.cafe;

import com.example.cafe.domain.order.entity.Orders;
import com.example.cafe.domain.order.entity.OrdersItem;
import com.example.cafe.domain.order.repository.OrdersItemRepository;
import com.example.cafe.domain.order.repository.OrdersRepository;
import com.example.cafe.domain.order.service.OrdersItemService;
import com.example.cafe.domain.order.service.OrdersService;
import com.example.cafe.domain.product.entity.Product;
import com.example.cafe.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Configuration
class CafeApplicationTests {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private OrdersRepository ordersRepository;

	@Autowired
	private OrdersService ordersService;

	@Autowired
	private OrdersItemRepository ordersItemRepository;
	@Autowired
	private OrdersItemService ordersItemService;

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
		assertThat(products.size()).isEqualTo(3);

	}

	@Test
	@DisplayName("저장되어 있는 모든 주문자 정보 조회")
	void findAllOrders() {

		List<Orders> ordersList = ordersRepository.findAll();

		assertThat(ordersList).isNotNull();
		assertThat(ordersList.size()).isGreaterThan(0);
	}

	@Test
	@DisplayName("저장되어 있는 모든 구매내역 조회")
	void findAllOrdersItems() {

		List<OrdersItem> ordersItemsList = ordersItemRepository.findAll();

		assertThat(ordersItemsList).isNotNull();
		assertThat(ordersItemsList.size()).isGreaterThan(0);
	}

	@Test
	@DisplayName("단일 상품 주문 후 이메일로 검증")
	void orderProductAndFindByEmail() {

		// 주문할 상품명, 이메일, 주소, 우편주소 하나 저장
		ArrayList<String> productName = new ArrayList<>();
		productName.add("카페라떼");
		String email = "test2@gmail.com";
		String address = "테스트용 주소2";
		ArrayList<Integer> quantity = new ArrayList<>();
		quantity.add(4);
		int postCode = 12345;

		// 상품을 주문
		ordersService.startOrders(productName, quantity, email, address, postCode);

		// 이메일로 찾아 변수에 저장
		Orders orders = ordersService.findByEmail("test2@gmail.com")
				.orElseThrow(() -> new IllegalArgumentException("이메일로 등록된 구매자를 찾을 수 없습니다."));

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

		/// 검증 ///
		// 이메일이 맞는지 확인
		assertThat(orders.getEmail()).isEqualTo(email);

		// 주소가 맞는지 확인
		assertThat(orders.getAddress()).isEqualTo(address);

		// 반환된 우편번호가 맞는지 확인
		assertThat(orders.getPostCode()).isEqualTo(postCode);

		/*
		 주문이 왔을때 해당 주문에 포함된 아메리카노에 대한 정보를 어디에서 가져올 것인가
		 상품정보는 BaseInitData() 세팅을 할것이다. -> 커피, 바나나, 사과 (DB -> 자바 메모리)
		 db에 잘저장이되었니(저장 Service를 통해서 저장하고 값을)? 세팅 이후에 자바메모리에서 잘 저장이 되니(해당 Entity의 setter를 통해서 확인)?
		 DB에서 자바메모리로 VO(바나나, 사과, 커피) 들을 옮기는 과정에서 자료구조가 있어야 되겠는데?
		 Product들의 정보를 담는 ProductsList가 있으면 되겠다.
			 수업중 Comment와 Comments 관계에서 사용되는 방법이 있습니다.
			 Product 객체 내부에 List<Product>를 지역변수로 설정하는 방법
		 검증하려는 목적이 중요
		 1. db 에 잘저장되어있는지
		 2. OrdersItem하고 Product 연결하는 메서드를 작성하였는데 그게 잘 작동하는지
		 검증 - 반환된 상품명이 아메리카노인지 확인
		 OrderItem -> orderProuductName으로 접근 뒤 검증
		 */

		// 수량이 맞는지 확인
		assertThat(orders.getOrdersItems().getFirst().getQuantity()).isEqualTo(quantity.getFirst());

		// 상품명이 카페라떼인지 확인
		List<OrdersItem> ordersItems = orders.getOrdersItems();
		OrdersItem savedOrdersItem = ordersItems.getFirst();

		assertThat(savedOrdersItem.getOrderProductName()).isEqualTo(productName.getFirst());
	}

//	@Test
//	@DisplayName("이메일로 단일 주문 내역 찾기 - 성공")
//	void findByEmailSuccess() {
//
//		// 주문할 상품명, 이메일, 주소, 우편주소 하나 저장
//		ArrayList<String> productName = new ArrayList<>();
//			productName.add("아메리카노");
//		String email = "test3@gmail.com";
//		String address = "테스트용 주소3";
//		ArrayList<Integer> quantity = new ArrayList<>();
//			quantity.add(1);
//		int postCode = 54867;
//
//		// 상품을 주문
//		ordersService.startOrders(productName, quantity, email, address, postCode);
//
//		// 저장해 둔 이메일
//		String email2 = "test3@gmail.com";
//
//		/// 검증 ///
//		// 이메일로 주문내역 찾아 변수에 저장
//		Orders foundOrder = ordersService.findOrderByEmail(email2);
//
//		// 조회 된 주문이 비어있는지 확인
//		assertThat(foundOrder).isNotNull();
//
//		// 조회 된 이메일이 똑같은지 확인
//		assertThat(foundOrder.getEmail()).isEqualTo(email2);
//
//		// 수량이 맞는지 확인
//		assertThat(foundOrder.getOrdersItems().getFirst().getQuantity()).isEqualTo(quantity.getFirst());
//
//		// 반환된 상품명이 아메리카노인지 확인
//		List<OrdersItem> ordersItems = foundOrder.getOrdersItems();
//		OrdersItem savedOrdersItem = ordersItems.getFirst();
//
//		assertThat(savedOrdersItem.getOrderProductName()).isEqualTo(productName.getFirst());
//	}

	@Test
	@DisplayName("이메일로 단일 주문 내역 찾기 - 실패")
	void findByEmailFail() {

		// 저장해 둔 이메일
		String email = "testX@gmail.com";

		// 조회 된 주문이 비어있는지 확인
		assertThatThrownBy(() -> {
			ordersService.findByEmail(email);
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
		ArrayList<Integer> quantity = new ArrayList<>();
		quantity.add(1);
		quantity.add(2);
		int postCode = 78645;

		// 주문하기
		ordersService.startOrders(productNames, quantity, email, address, postCode);

		// 이메일로 찾아 변수에 저장
		Orders foundProduct = ordersService.findByEmail("test4@gmail.com")
				.orElseThrow(() -> new IllegalArgumentException("이메일로 등록된 구매자를 찾을 수 없습니다."));

		/// 검증 ///
		// 이메일
		assertThat(foundProduct.getEmail()).isEqualTo(email);

		// 주소
		assertThat(foundProduct.getAddress()).isEqualTo(address);

		// 우편번호
		assertThat(foundProduct.getPostCode()).isEqualTo(postCode);

		List<OrdersItem> ordersItems = foundProduct.getOrdersItems();

		// 주문한 아이템 사이즈가 2개인지 확인
		assertThat(ordersItems).hasSize(2);

		// 수량
		for (OrdersItem ordersItem : ordersItems) {

			// 각 음료의 이름과 수량을 담음
			String orderProductName = ordersItem.getOrderProductName();
			int orderQuantity = ordersItem.getQuantity();

			// 각각의 수량이 맞는 지 확인
			if (orderProductName.equals("아메리카노")) {
				assertThat(orderQuantity).isEqualTo(1);
			} else if (orderProductName.equals("카페라떼")) {
				assertThat(orderQuantity).isEqualTo(2);
			} else {
				System.out.println("주문 상품 이름이 수량과 다릅니다 : " + orderProductName);
			}

			// 주문 음료 이름
			assertThat(productNames.contains(orderProductName)).isTrue();
		}
	}

	@Test
	@DisplayName("날짜 생성 및 확인")
	void orderDate() {
		// 주문할 상품명, 이메일, 주소, 우편주소 하나 저장
		ArrayList<String> productName = new ArrayList<>();
		productName.add("카페라떼");
		String email = "test5@gmail.com";
		String address = "테스트용 주소5";
		ArrayList<Integer> quantity = new ArrayList<>();
		quantity.add(2);
		int postCode = 97283;

		// 현재 날짜와 시간 생성 (나노초 차이 때문에 500나노초 더하기)
		LocalDateTime currentTime = LocalDateTime.now().plusNanos(500);

		// 주문 하기
		ordersService.startOrders(productName, quantity, email, address, postCode);

		// 이메일로 찾아 변수에 저장
		Orders foundProduct = ordersService.findByEmail("test5@gmail.com")
				.orElseThrow(() -> new IllegalArgumentException("이메일로 등록된 구매자를 찾을 수 없습니다."));

		// 날짜가 같은지 확인
		LocalDateTime orderedDateTime = foundProduct.getOrdersItems().getFirst().getOrderDate();

		assertThat(orderedDateTime).isNotNull();
		assertThat(orderedDateTime).isEqualToIgnoringNanos(currentTime);

	}

	@Test
	@DisplayName("오후 2시부터 다음날 오후 2시까지의 한 구매자의 주문 내역 확인")
	void findOrderProduct2pmBefore() {

		// 구매자 생성
		System.out.println("구매자 저장");
		Orders orders = ordersService.add("testemail@mail.com", "서울시 마포구", 96587);

		// 주문할 상품 생성
		ArrayList<String> productName = new ArrayList<>();
		productName.add("카페라떼");
		productName.add("아이스티");
		ArrayList<Integer> quantity = new ArrayList<>();
		quantity.add(2);
		quantity.add(4);

		// 당일 오후 2시 이전에 주문 (카페라떼)
		OrdersItem ordersItemBefore2pm = OrdersItem.builder()
				.orders(orders)
				.orderProductId(2L)
				.orderProductName("카페라떼")
				.quantity(2)
				// (임시 값이므로 변경 필수)
				.orderDate(LocalDateTime.of(2025, 2, 20, 8, 30))
				.build();

		System.out.println("카페라떼 저장 b");
		ordersItemRepository.save(ordersItemBefore2pm);
		orders.addOrdersItem(ordersItemBefore2pm);

		// 당일 오후 2시 이후에 주문 (아이스티)
		OrdersItem ordersItemAfter2pm = OrdersItem.builder()
				.orders(orders)
				.orderProductId(3L)
				.orderProductName("아이스티")
				.quantity(4)
				// (임시 값이므로 변경 필수)
				.orderDate(LocalDateTime.of(2025, 2, 21, 14, 30))
				.build();

		System.out.println("아이스티 저장 c");
		ordersItemRepository.save(ordersItemAfter2pm);
		orders.addOrdersItem(ordersItemAfter2pm);

		System.out.println("추가한 시간" + ordersItemAfter2pm.getOrderDate());


		// 변수 ctime 설정 (임시 값이므로 변경 필수)
		OrdersItemService.setCtime(14);

		// 배송 메서드 실행
		List<OrdersItem> deliveryOrders = ordersItemService.findOrdersDuring2pm();
		/*
		해당 메서드 로직에 스케줄러가 작동되는 시간을 조절하는 변수가 (* * * * *)
		* */

		/// 검증 ///
		System.out.println("{ 배송 대상 주문 목록 테스트 }");

		if (deliveryOrders.isEmpty()) {
			System.out.println("배송할 주문이 없습니다.");
		} else {
			for (OrdersItem ordersItem : deliveryOrders) {
				System.out.println("주문자 이메일: " + ordersItem.getOrders().getEmail());
				System.out.print("주문 상품) ");

				System.out.println("상품명: " + ordersItem.getOrderProductName() +
						", 갯수: " + ordersItem.getQuantity() +
						", 구매 시간: " + ordersItem.getOrderDate());
			}
		}

		// 비어있는지 확인
		assertThat(deliveryOrders).isNotNull();

		// 크기 확인
		assertThat(deliveryOrders.size()).isEqualTo(2);

		// 주문 가져오기
		OrdersItem filteredDeliveryOrders = deliveryOrders.getFirst();
//		OrdersItem filteredDeliveryOrdersItems = filteredDeliveryOrders.getOrdersItems();

	}

	@Test
	@DisplayName("상품 배달완료 확인")
	void deliveryComplete() {

		// 구매자 생성
		Orders orders = ordersService.add("testemail@testmail.com", "서울시 마포구", 96587);

		// 주문 상품 정보 생성 및 구매
		OrdersItem ordersItemAfter2pm = OrdersItem.builder()
				.orders(orders)
				.orderProductId(3L)
				.orderProductName("아이스티")
				.quantity(2)
				.orderDate(LocalDateTime.now().minusDays(2))
				.build();

		// DB에 영속화 및 주문목록에 저장
		ordersItemRepository.save(ordersItemAfter2pm);
		orders.addOrdersItem(ordersItemAfter2pm);

		// 배송 메서드 실행
		ordersItemService.startDelivery();

		/// 검증 ///
		// 구매자의 이메일로 주문한 상품 찾기
		Optional<OrdersItem> foundOrdersItemBefore2pm = ordersItemService.findOrdersItemByOrdersEmail("testemail@testmail.com");

		// 주문 상품이 존재하는지 확인
		assertThat(foundOrdersItemBefore2pm).isPresent();
		OrdersItem ordersItemBefore2pm = foundOrdersItemBefore2pm.get();

		// 주문시간이 오후 2시 이전임으로 배송완료(false) 상태
		assertThat(ordersItemBefore2pm.isCompleted())
				.isFalse();

		// 출력
		System.out.println("{ 배송 대상 주문 목록 테스트 }");

		if (!ordersItemBefore2pm.isCompleted()) {
			System.out.println("배송 완료 상품"
					+ "\n상품명: " + ordersItemBefore2pm.getOrderProductName()
					+ "\n상품 수량: " + ordersItemBefore2pm.getQuantity()
					+ "\n상품 구매 시간: " + ordersItemBefore2pm.getOrderDate());
		} else {
			System.out.println("배송 중인 상품 "
					+ "\n상품명: " + ordersItemBefore2pm.getOrderProductName()
					+ "\n상품 수량: " + ordersItemBefore2pm.getQuantity()
					+ "\n상품 구매 시간: " + ordersItemBefore2pm.getOrderDate());
		}
	}

	@Test
	@DisplayName("상품 배달중 확인")
	void deliveryProcessing() {

		// 구매자 생성
		Orders orders = ordersService.add("testemail@testmail.com", "서울시 마포구", 96587);

		// 주문 상품 정보 생성 및 구매
		OrdersItem ordersItemAfter2pm = OrdersItem.builder()
				.orders(orders)
				.orderProductId(2L)
				.orderProductName("카페라떼")
				.quantity(5)
				.orderDate(LocalDateTime.now().plusMinutes(10))
				.build();

		// DB에 영속화 및 주문목록에 저장
		ordersItemRepository.save(ordersItemAfter2pm);
		orders.addOrdersItem(ordersItemAfter2pm);

		// 배송 메서드 실행
		ordersItemService.startDelivery();

		/// 검증 ///
		// 구매자의 이메일로 주문한 상품 찾기
		Optional<OrdersItem> foundOrdersItemBefore2pm = ordersItemService.findOrdersItemByOrdersEmail("testemail@testmail.com");

		// 주문 상품이 존재하는지 확인
		assertThat(foundOrdersItemBefore2pm).isPresent();
		OrdersItem ordersItemBefore2pm = foundOrdersItemBefore2pm.get();

		// 주문시간이 오후 2시 이후임으로 배송중(true) 상태
		assertThat(ordersItemBefore2pm.isCompleted())
				.isTrue();

		// 출력
		System.out.println("{ 배송 대상 주문 목록 테스트 }");

		if (!ordersItemBefore2pm.isCompleted()) {
			System.out.println("배송 완료 상품"
					+ "\n상품명: " + ordersItemBefore2pm.getOrderProductName()
					+ "\n상품 수량: " + ordersItemBefore2pm.getQuantity()
					+ "\n상품 구매 시간: " + ordersItemBefore2pm.getOrderDate());
		} else {
			System.out.println("배송 중인 상품 "
					+ "\n상품명: " + ordersItemBefore2pm.getOrderProductName()
					+ "\n상품 수량: " + ordersItemBefore2pm.getQuantity()
					+ "\n상품 구매 시간: " + ordersItemBefore2pm.getOrderDate());
		}
	}

	@Test
	@DisplayName("상품 배달중, 배달 완료 확인")
	void deliveryProcessingAndComplete() {

		// 구매자 생성
		Orders orders = ordersService.add("testemail@testmail.com", "서울시 마포구", 96587);

		// 주문 상품 정보 생성 및 구매 (아메리카노 오후 2시 이전에 주문)
		OrdersItem ordersItemBefore2pm = OrdersItem.builder()
				.orders(orders)
				.orderProductId(1L)
				.orderProductName("아메리카노")
				.quantity(10)
				.orderDate(LocalDateTime.now().minusDays(1))
				.build();

		// DB에 영속화 및 주문목록에 저장
		ordersItemRepository.save(ordersItemBefore2pm);
		orders.addOrdersItem(ordersItemBefore2pm);

		// 주문 상품 정보 생성 및 구매 (카페라떼 오후 2시 이후에 주문)
		OrdersItem ordersItemAfter2pm = OrdersItem.builder()
				.orders(orders)
				.orderProductId(2L)
				.orderProductName("카페라떼")
				.quantity(1)
				.orderDate(LocalDateTime.now().plusMinutes(30))
				.build();

		// DB에 영속화 및 주문목록에 저장
		ordersItemRepository.save(ordersItemAfter2pm);
		orders.addOrdersItem(ordersItemAfter2pm);

		// 배송 메서드 실행
		ordersItemService.startDelivery();

		/// 검증 ///
		// 구매자의 이메일로 구매자 찾기
		Optional<Orders> foundOrdersItemsByEmail = ordersService.findByEmail("testemail@testmail.com");

		// 주문 상품 목록 가져오기
		List<OrdersItem> foundOrdersItems = foundOrdersItemsByEmail
				.map(Orders::getOrdersItems) // 구매자(Orders)가 존재하면 주문 상품 가져옴
				.orElse(Collections.emptyList()); // 구매자(Orders)가 없으면 빈 목록을 넣음

		// 주문 상품 목록에서 아메리카노, 카페라떼 상품 이름으로 찾기
		OrdersItem americano = null;
		OrdersItem cafelatte = null;

		for (OrdersItem ordersItem : foundOrdersItems) {
			if ("아메리카노".equals(ordersItem.getOrderProductName())) {
				americano = ordersItem;
			} else if ("카페라떼".equals(ordersItem.getOrderProductName())) {
				cafelatte = ordersItem;
			}
		}
		
		// 2시 이전 주문인 아메리카노는 배송완료 상태
		assertThat(americano).isNotNull();
		assertThat(americano.isCompleted()).isFalse();

		// 2시 이후 주문인 카페라떼는 배송중 상태
		assertThat(cafelatte).isNotNull();
		assertThat(cafelatte.isCompleted()).isTrue();

		// 출력
		System.out.println("{ 배송 대상 주문 목록 테스트 }");

		// 출력 (배송 완료 상품 정보 - 아메리카노)
		System.out.println("{ 배송 완료 상품 정보 - 아메리카노 (2시 이전 주문) }");
		System.out.println("상품명: " + americano.getOrderProductName());
		System.out.println("상품 수량: " + americano.getQuantity());
		System.out.println("상품 구매 시간: " + americano.getOrderDate());
		System.out.println("배송 완료 상태: " + americano.isCompleted());

		// 출력 (배송 중 상품 정보 - 카페라떼)
		System.out.println("\n{ 배송 중 상품 정보 - 카페라떼 (2시 이후 주문) }");
		System.out.println("상품명: " + cafelatte.getOrderProductName());
		System.out.println("상품 수량: " + cafelatte.getQuantity());
		System.out.println("상품 구매 시간: " + cafelatte.getOrderDate());
		System.out.println("배송 중 상태: " + cafelatte.isCompleted());
	}

//	@Test
//	@DisplayName("주문 삭제 후 확인")
//	void deleteOrder() {
//
//	}

//	@Test
//	@DisplayName("이미 구매한 상품에서 상품의 갯수 차감")
//	void reduceProduct() {
//
//	}

//	@Test
//	@DisplayName("")
//	void findOrderProduct2pmBefore() {
//
//	}
}
