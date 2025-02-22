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
	@DisplayName("구매자 이메일로 구매자의 모든 상품들을 조회")
	void findAllOrdersItemsByOrdersEmail() {

		// 주문할 상품 이름 2개, 이메일, 주소, 수량, 우편번호 저장
		ArrayList<String> productNames = new ArrayList<>();
			productNames.add("카페라떼");
			productNames.add("아이스티");
		String email = "test10@gmail.com";
		String address = "테스트용 주소10";
		ArrayList<Integer> quantity = new ArrayList<>();
			quantity.add(5);
			quantity.add(8);
		int postCode = 3782543;

		// 주문하기
		ordersService.startOrders(productNames, quantity, email, address, postCode);

		// 이메일로 찾아 변수에 상품 리스트 저장
		List<OrdersItem> ordersItems = ordersItemRepository.findOrdersItemByOrdersEmail(email);

		/// 검증 ///
		// 출력
		System.out.println("구매자 이메일: " + ordersItems.getFirst().getOrders().getEmail());
		System.out.println("구매 내역");

		for (OrdersItem ordersItem : ordersItems) {
			System.out.println("상품명: " + ordersItem.getOrderProductName());
			System.out.println("수량: " + ordersItem.getQuantity());
			System.out.println("-----------------------------");
		}

		// 사이즈가 2인지 확인
		assertThat(ordersItems)
				.isNotNull()
				.isNotEmpty()
				.hasSize(2);

		// 상품명, 수량이 맞는지 확인
		assertThat(ordersItems.getFirst().getOrderProductName()).isEqualTo(productNames.getFirst());
		assertThat(ordersItems.getFirst().getQuantity()).isEqualTo(quantity.getFirst());
	}

	@Test
	@DisplayName("주문내역 id로 주문 삭제 검증")
	void deleteOrdersItemByOrdersItemId() {

		// 구매자 생성
		Orders orders = ordersService.add("testemail@mail.com", "서울시 마포구", 96587);

		OrdersItem ordersItem = OrdersItem.builder()
				.orders(orders)
				.orderProductId(2L)
				.orderProductName("카페라떼")
				.quantity(2)
				.build();

		// 주문하기
		ordersItemRepository.save(ordersItem);
		orders.addOrdersItem(ordersItem);

		// 주문내역 id로 삭제
		boolean isDeleted = ordersItemService.deleteOrdersItemByOrdersItemId(2L);

		/// 검증 ///
		// 삭제 성공 여부
		assertThat(isDeleted).isTrue();
	}

	@Test
	@DisplayName("이메일로 주문을 찾아 삭제 후 확인")
	void deleteOrdersItemByOrdersEmail() {

		// 주문할 상품 이름 2개, 이메일, 주소, 수량, 우편번호 저장
		ArrayList<String> productNames = new ArrayList<>();
			productNames.add("아이스티");
			productNames.add("아이스티");
		String email = "test10@gmail.com";
		String address = "테스트용 주소10";
		ArrayList<Integer> quantity = new ArrayList<>();
			quantity.add(2);
			quantity.add(7);
		int postCode = 3782543;

		// 주문하기
		ordersService.startOrders(productNames, quantity, email, address, postCode);

		// 이메일 기반으로 주문 삭제
		boolean isDeleted = ordersItemService.deleteByOrders_Email(email);
		assertThat(isDeleted).isTrue();

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

		/// 검증 ///
		// 이메일이 맞는지 확인
		assertThat(orders.getEmail()).isEqualTo(email);

		// 주소가 맞는지 확인
		assertThat(orders.getAddress()).isEqualTo(address);

		// 반환된 우편번호가 맞는지 확인
		assertThat(orders.getPostCode()).isEqualTo(postCode);

		// 수량이 맞는지 확인
		assertThat(orders.getOrdersItems().getFirst().getQuantity()).isEqualTo(quantity.getFirst());

		// 상품명이 카페라떼인지 확인
		List<OrdersItem> ordersItems = orders.getOrdersItems();
		OrdersItem savedOrdersItem = ordersItems.getFirst();

		assertThat(savedOrdersItem.getOrderProductName()).isEqualTo(productName.getFirst());
	}

	@Test
	@DisplayName("이메일로 단일 주문 내역 찾기 - 실패")
	void findByEmailFail() {

		// 저장해 둔 이메일
		String email = "testX@gmail.com";

		// 이메일로 주문 검색
		Optional<Orders> findOrders = ordersRepository.findByEmail(email);

		// 조회 된 주문이 비어있는지 확인
		assertThat(findOrders).isEmpty();

		// 출력
		System.out.println("찾으시려는 주문자가 없습니다.");
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

		/// 검증 ///
		// 날짜가 같은지 확인
		LocalDateTime orderedDateTime = foundProduct.getOrdersItems().getFirst().getOrderDate();

		assertThat(orderedDateTime).isNotNull();
		assertThat(orderedDateTime).isEqualToIgnoringNanos(currentTime);

	}

//	@Test
//	@DisplayName("오후 2시부터 다음날 오후 2시까지의 한 구매자의 주문 내역 확인")
//	void findOrderProduct2pmBefore() {
//
//		// 구매자 생성
//		System.out.println("구매자 저장");
//		Orders orders = ordersService.add("testemail@mail.com", "서울시 마포구", 96587);
//
//		// 현재 시간
//		LocalDateTime currentTime = LocalDateTime.now();
//
//		// 주문할 상품 생성
//		ArrayList<String> productName = new ArrayList<>();
//			productName.add("카페라떼");
//			productName.add("아이스티");
//		ArrayList<Integer> quantity = new ArrayList<>();
//			quantity.add(2);
//			quantity.add(4);
//
//		// 당일 오후 2시 이전에 주문 (카페라떼)
//		OrdersItem ordersItemBefore2pm = OrdersItem.builder()
//				.orders(orders)
//				.orderProductId(2L)
//				.orderProductName("카페라떼")
//				.quantity(2)
//				// 현재 시간 기준으로 오후 2시 이전 (오후 1시)
//				.orderDate(currentTime.withHour(13).withMinute(30))
//				.build();
//
//		System.out.println("카페라떼 저장");
//		ordersItemRepository.save(ordersItemBefore2pm);
//		orders.addOrdersItem(ordersItemBefore2pm);
//
//		// 당일 오후 2시 이후에 주문 (아이스티)
//		OrdersItem ordersItemAfter2pm = OrdersItem.builder()
//				.orders(orders)
//				.orderProductId(3L)
//				.orderProductName("아이스티")
//				.quantity(4)
//				// 현재 시간 기준으로 오후 2시 이후 (오후 3시)
//				.orderDate(currentTime.withHour(15).withMinute(30))
//				.build();
//
//		System.out.println("아이스티 저장");
//		ordersItemRepository.save(ordersItemAfter2pm);
//		orders.addOrdersItem(ordersItemAfter2pm);
//
//		System.out.println("추가한 시간" + ordersItemAfter2pm.getOrderDate());
//
//		// 배송 메서드 실행
//		List<OrdersItem> deliveryOrders = ordersItemService.findOrdersDuring2pm();
//
//		/// 검증 ///
//		System.out.println("{ 배송 대상 주문 목록 테스트 }");
//
//		if (deliveryOrders.isEmpty()) {
//			System.out.println("배송할 주문이 없습니다.");
//		} else {
//			for (OrdersItem ordersItem : deliveryOrders) {
//				System.out.println("주문자 이메일: " + ordersItem.getOrders().getEmail());
//				System.out.print("{ 주문 상품 }");
//
//				System.out.println("상품명: " + ordersItem.getOrderProductName() +
//						"\n상품 갯수: " + ordersItem.getQuantity() +
//						"\n상품 구매 시간: " + ordersItem.getOrderDate());
//			}
//		}
//
//		// 비어있는지 확인
//		assertThat(deliveryOrders).isNotNull();
//
//		// 크기 확인
//		assertThat(deliveryOrders.size()).isEqualTo(2);
//
//		// 주문 가져오기 (배송 중인 주문이 1개 이상인지 확인)
//		assertThat(deliveryOrders.size()).isGreaterThanOrEqualTo(1);
//		OrdersItem ordersItem = deliveryOrders.getFirst();
//
//		//
//	}

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
		List<OrdersItem> foundOrdersItemBefore2pm = ordersItemService.findOrdersItemByOrdersEmail("testemail@testmail.com");

		// 상품 변수에 저장
		OrdersItem ordersItemBefore2pm = foundOrdersItemBefore2pm.get(0);

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

		// 주문 상품이 존재하는지 확인
		assertThat(foundOrdersItemBefore2pm).isNotEmpty();

		// 주문시간이 오후 2시 이전임으로 배송완료(false) 상태
		assertThat(ordersItemBefore2pm.isCompleted())
				.isFalse();
	}

	@Test
	@DisplayName("상품 배달중 확인")
	void deliveryProcessing() {

		// 구매자 생성
		Orders orders = ordersService.add("testemail11@testmail.com", "서울시 마포구", 96587);

		// 주문 상품 정보 생성 (카페라떼 다음날 오후 3시 30분에 주문)
		OrdersItem ordersItemAfter2pm = OrdersItem.builder()
				.orders(orders)
				.orderProductId(2L)
				.orderProductName("카페라떼")
				.quantity(5)
				.orderDate(LocalDateTime.now().plusDays(1).withHour(15).withMinute(30))
				.build();

		// DB에 영속화 및 주문목록에 저장
		ordersItemRepository.save(ordersItemAfter2pm);
		orders.addOrdersItem(ordersItemAfter2pm);

		// 배송 메서드 실행
		ordersItemService.startDelivery();

		/// 검증 ///
		// 구매자의 이메일로 주문한 상품 찾기
		List<OrdersItem> foundOrdersItemAfter2pm = ordersItemService.findOrdersItemByOrdersEmail("testemail11@testmail.com");

		// 상품 가져와 변수에 저장
		OrdersItem ordersItemBefore2pm = foundOrdersItemAfter2pm.getFirst();

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

		// 주문 상품이 존재하는지 확인
		assertThat(foundOrdersItemAfter2pm).isNotEmpty();

		// 주문시간이 오늘 오후 2시 이후임으로 배송중(true) 상태
		assertThat(ordersItemBefore2pm.isCompleted())
				.isTrue();
	}

	@Test
	@DisplayName("상품 배달중, 배달 완료 확인")
	void deliveryProcessingAndComplete() {

		// 구매자 생성
		Orders orders = ordersService.add("testemail7@testmail.com", "서울시 마포구", 96587);

		// 주문 상품 정보 생성 및 구매 (아메리카노 어제 오후 2시 이전에 주문)
		OrdersItem ordersItemBefore2pm = OrdersItem.builder()
				.orders(orders)
				.orderProductId(1L)
				.orderProductName("아메리카노")
				.quantity(10)
				.orderDate(LocalDateTime.now().minusDays(1).minusMinutes(10))
				.build();

		// DB에 영속화 및 주문목록에 저장
		ordersItemRepository.save(ordersItemBefore2pm);
		orders.addOrdersItem(ordersItemBefore2pm);

		// 주문 상품 정보 생성 및 구매 (카페라떼 다음날 오후 2시 30분에 주문)
		OrdersItem ordersItemAfter2pm = OrdersItem.builder()
				.orders(orders)
				.orderProductId(2L)
				.orderProductName("카페라떼")
				.quantity(1)
				.orderDate(LocalDateTime.now().withHour(14).plusMinutes(30))
				.build();

		// DB에 영속화 및 주문목록에 저장
		ordersItemRepository.save(ordersItemAfter2pm);
		orders.addOrdersItem(ordersItemAfter2pm);

		// 배송 메서드 실행
		ordersItemService.startDelivery();

		/// 검증 ///
		// 구매자의 이메일로 구매자 찾기
		Optional<Orders> foundOrdersByEmail = ordersService.findByEmail("testemail7@testmail.com");

		// 주문 상품 목록 가져오기
		List<OrdersItem> deliveryOrdersItems = foundOrdersByEmail
				.map(Orders::getOrdersItems) // 구매자(Orders)가 존재하면 주문 상품 가져옴
				.orElse(Collections.emptyList()); // 구매자(Orders)가 없으면 빈 목록을 넣음

		// 주문 상품 목록에서 아메리카노, 카페라떼 상품 이름으로 찾기
		OrdersItem americano = null;
		OrdersItem cafelatte = null;

		for (OrdersItem ordersItem : deliveryOrdersItems) {
			if ("아메리카노".equals(ordersItem.getOrderProductName())) {
				americano = ordersItem;
			} else if ("카페라떼".equals(ordersItem.getOrderProductName())) {
				cafelatte = ordersItem;
			}
		}

		// 배송 목록 출력
		System.out.println("{ 배송 대상 주문 목록 테스트 }");

		// 출력 (배송 완료 상품 정보 - 아메리카노)
		System.out.println("{ 배송 완료 상품 정보 - 아메리카노 (어제 2시 이전 주문) }");
		System.out.println("상품명: " + americano.getOrderProductName());
		System.out.println("상품 수량: " + americano.getQuantity());
		System.out.println("상품 구매 시간: " + americano.getOrderDate());
		System.out.println("배송 상태 (true면 배송중 / false면 배송완료): " + americano.isCompleted());

		// 출력 (배송 중 상품 정보 - 카페라떼)
		System.out.println("\n{ 배송 중 상품 정보 - 카페라떼 (오늘 2시 이전 주문) }");
		System.out.println("상품명: " + cafelatte.getOrderProductName());
		System.out.println("상품 수량: " + cafelatte.getQuantity());
		System.out.println("상품 구매 시간: " + cafelatte.getOrderDate());
		System.out.println("배송 상태 (true면 배송중 / false면 배송완료): " + cafelatte.isCompleted());

		// 2시 이전 주문인 아메리카노는 배송완료 상태
		assertThat(americano).isNotNull();
		assertThat(americano.isCompleted()).isFalse();

		// 2시 이후 주문인 카페라떼는 배송중 상태
		assertThat(cafelatte).isNotNull();
		assertThat(cafelatte.isCompleted()).isTrue();
	}

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
