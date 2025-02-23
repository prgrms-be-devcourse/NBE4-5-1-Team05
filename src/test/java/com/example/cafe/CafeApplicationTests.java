package com.example.cafe;

import com.example.cafe.domain.order.entity.Orders;
import com.example.cafe.domain.order.entity.OrdersItem;
import com.example.cafe.domain.order.repository.OrdersItemRepository;
import com.example.cafe.domain.order.repository.OrdersRepository;
import com.example.cafe.domain.order.service.OrdersItemService;
import com.example.cafe.domain.order.service.OrdersService;
import com.example.cafe.domain.product.entity.Product;
import com.example.cafe.domain.product.repository.ProductRepository;
import com.example.cafe.domain.product.service.ProductService;
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
    @Autowired
    private ProductService productService;

	@Test
	@DisplayName("출력 시험")
	void contextLoads() {
		System.out.println("Hello World");
	}

	@Test
	@DisplayName("모든 상품 조회")
	void findAllProducts() {

		List<Product> products = productRepository.findAll();

		System.out.println("{ 모든 상품 목록 }");
		for (Product product : products) {
			System.out.println("상품명: " + product.getName());
			System.out.println("상품 가격: " + product.getName());
			System.out.println("-------------------------------");
		}

		assertThat(products).isNotNull();
		assertThat(products.size()).isEqualTo(3);

	}

	@Test
	@DisplayName("모든 구매자 정보 조회")
	void findAllOrders() {

		List<Orders> ordersList = ordersRepository.findAll();

		System.out.println("{ 모든 구매자 목록 }");
		for (Orders orders : ordersList) {
			System.out.println("구매자 이메일: " +orders.getEmail());
			System.out.println("구매자 주소: " +orders.getAddress());
			System.out.println("구매자 우편번호: " +orders.getPostCode());
			System.out.println("-------------------------------");
		}

		assertThat(ordersList).isNotNull();
		assertThat(ordersList.size()).isGreaterThan(0);
	}

	@Test
	@DisplayName("모든 구매내역 조회")
	void findAllOrdersItems() {

		List<OrdersItem> ordersItemsList = ordersItemRepository.findAll();

		System.out.println("{ 모든 구매내역 목록 }");
		for (OrdersItem ordersItem : ordersItemsList) {
			System.out.println("<구매자 정보>");
			System.out.println("구매자 이메일: " + ordersItem.getOrders().getEmail());
			System.out.println("구매자 주소: " + ordersItem.getOrders().getAddress());
			System.out.println("구매자 우편주소: " + ordersItem.getOrders().getPostCode());
			System.out.println();
			System.out.println("<구매 내역 정보>");
			System.out.println("주문한 상품명: " + ordersItem.getOrderProductName());
			System.out.println("주문한 상품의 수량: " + ordersItem.getOrderProductPrice());
			System.out.println("주문한 상품의 총 가격: " + ordersItem.getOrderProductPrice());
			System.out.println("주문 날짜: " + ordersItem.getOrderDate());
			System.out.println("주문 완료 여부 (false면 배송 완료 | true면 배송중): "
					+ ordersItem.isCompleted());
			System.out.println("-------------------------------");
		}

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

		// 이메일로 찾아 변수에 저장
		List<OrdersItem> ordersItems = ordersItemRepository.findOrdersItemByOrdersEmail(email);

		/// 검증 ///
		// 출력
		System.out.println("구매자 이메일: " + ordersItems.get(0).getOrders().getEmail());
		System.out.println("구매 내역");

		for (OrdersItem ordersItem : ordersItems) {
			System.out.println("상품명: " + ordersItem.getOrderProductName());
			System.out.println("수량: " + ordersItem.getQuantity());
			System.out.println("-----------------------------");
		}

		// 값을 갖고 있는지 확인
		assertThat(ordersItems).isNotEmpty();

		// 상품명, 수량이 맞는지 확인
		assertThat(ordersItems.get(0).getOrderProductName()).isEqualTo(productNames.get(0));
		assertThat(ordersItems.get(0).getQuantity()).isEqualTo(quantity.get(0));

		assertThat(ordersItems.get(1).getOrderProductName()).isEqualTo(productNames.get(1));
		assertThat(ordersItems.get(1).getQuantity()).isEqualTo(quantity.get(1));
	}

	@Test
	@DisplayName("상품명으로 상품 찾기")
	void findProductByName() {

		// 상품명으로 상품 찾기
		Product product = productService.findByName("아메리카노");

		/// 검증 ///
		assertThat(product).isNotNull();
		assertThat(product.getName()).isEqualTo("아메리카노");
	}

	@Test
	@DisplayName("특정 상품의 가격으로 상품 찾기")
	void findProductByPrice() {

		// 상품 가격으로 상품 찾기 (5500원인 카페라떼 찾기)
		Optional<Product> product = productService.findByPrice(5500);

		/// 검증 ///
		assertThat(product).isNotNull();
		assertThat(product.get().getName()).isEqualTo("카페라떼");
		assertThat(product.get().getPrice()).isEqualTo(5500);
	}

	@Test
	@DisplayName("특정 상품의 이름으로 상품 삭제")
	void deleteProduct() {

		// 샘플 상품 데이터 추가
		productService.add("핫도그", 50000, "임시");

		// 삭제
		productService.deleteByName("핫도그");

		/// 검증 ///
		// 출력
		List<Product> ProductList = productRepository.findAll();

		System.out.println("{ 상품 목록 }");
		for (Product product : ProductList) {
			System.out.println("상품명: " + product.getName());
			System.out.println("가격: " + product.getPrice());
			System.out.println("---------------------------------------");
		}

		assertThat(productRepository.findByName("핫도그")).isEmpty();
	}

	@Test
	@DisplayName("상품 가격으로 상품 삭제")
	void deleteProductByPrice() {

		// 샘플 상품 데이터 추가
		productService.add("초코칩 쿠키", 2500, "임시");

		// 삭제
		productService.deleteByPrice(2500);

		/// 검증 ///
		// 출력
		List<Product> ProductList = productRepository.findAll();

		System.out.println("{ 상품 목록 }");
		for (Product product : ProductList) {
			System.out.println("상품명: " + product.getName());
			System.out.println("가격: " + product.getPrice());
			System.out.println("---------------------------------------");
		}

		assertThat(productRepository.findByName("초코칩 쿠키")).isEmpty();

	}

	@Test
	@DisplayName("상품명을 받아 상품 수정")
	void modifyProductByProduct() {
		
		// 샘플 상품 데이터 추가
		productService.add("아이스 바닐라 라떼", 6000, "임시");

		// 상품명 수정
		Product product = productService.findByName("아이스 바닐라 라떼");
		productService.modifyProduct(product, "아인슈페너", 50000, null);

		/// 검증 ///
		// 출력
		List<Product> ProductList = productRepository.findAll();

		System.out.println("{ 상품 목록 }");
		for (Product products : ProductList) {
			System.out.println("상품명: " + products.getName());
			System.out.println("상품 가격: " + products.getPrice());
			System.out.println("상품 이미지: " + products.getImageURL());
			System.out.println("---------------------------------------");
		}

		Product modifiedProduct = productService.findByName("아인슈페너");

		// 상품명, 상품 가격, 상품 이미지(수정 안함) 확인
		assertThat(modifiedProduct).isNotNull();
		assertThat(modifiedProduct.getName()).isEqualTo("아인슈페너");
		assertThat(modifiedProduct.getPrice()).isEqualTo(50000);
		assertThat(modifiedProduct.getImageURL()).isEqualTo("임시");
		
	}

	@Test
	@DisplayName("상품명을 받아 상품 가격 수정")
	void modifyProductByProductPrice() {

		// 샘플 상품 데이터 추가
		productService.add("아이스 바닐라 라떼", 6000, "임시");

		// 새 상품명
		String newProductName = "아인슈페너";

		// 새 상품 가격
		int newProductPrice = 6500;

		// 상품명 수정
		Product product = productService.findByName("아이스 바닐라 라떼");
//		productService.modifyProductName(, "");

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

		// 주문하기 및 주문 내역 id 획득
		OrdersItem savedOrdersItem = ordersItemRepository.save(ordersItem);
		Long ordersItemId = savedOrdersItem.getOrdersItemId();
		orders.addOrdersItem(savedOrdersItem);

		// 주문내역 id로 삭제
		ordersItemService.deleteOrdersItemByOrdersItemId(ordersItemId);

		/// 검증 ///
		// 삭제 성공 여부
		Optional<OrdersItem> deletedOrdersItem = ordersItemRepository.findById(ordersItemId);
		assertThat(deletedOrdersItem).isEmpty();
	}

	@Test
	@DisplayName("이메일로 주문을 찾아 삭제 후 확인")
	void deleteOrdersItemByOrdersEmail() {

		// 주문할 상품 이름, 이메일, 주소, 수량, 우편번호 저장
		ArrayList<String> productNames = new ArrayList<>();
			productNames.add("아이스티");
		String email = "test20@gmail.com";
		String address = "테스트용 주소20";
		ArrayList<Integer> quantity = new ArrayList<>();
			quantity.add(2);
		int postCode = 3782543;

		// 주문하기
		ordersService.startOrders(productNames, quantity, email, address, postCode);

		// 이메일 기반으로 주문 삭제
		ordersItemService.deleteByOrdersEmail(email);

		/// 검증 ///
		// 주문한 상품들 모두 출력
		List<OrdersItem> ordersItemsList = ordersItemRepository.findAll();

		System.out.println("{ 주문된 상품 목록 }");
		for (OrdersItem ordersItem : ordersItemsList) {
			System.out.println("이메일: " + ordersItem.getOrders().getEmail());
			System.out.println("상품명" + ordersItem.getOrderProductName());
			System.out.println("상품 갯수: " + ordersItem.getQuantity());
			System.out.println("---------------------------------------");
		}

		// DB에서 주문 상품 아이템이 삭제되었는지 확인
		List<OrdersItem> ordersItems = ordersItemRepository.findOrdersItemByOrdersEmail(email);
		System.out.println("TDD 검증용 OrdersItem 갯수: " + ordersItems.size());
		assertThat(ordersItems).isEmpty();
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
		// 구매자의 이메일로 주문한 상품 찾기 및 변수에 저장
		List<OrdersItem> ordersItems = ordersItemService.findOrdersItemByOrdersEmail(orders.getEmail());
		OrdersItem ordersItemBefore2pm = ordersItems.getFirst();

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
		assertThat(ordersItemBefore2pm).isNotNull();

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
		List<OrdersItem> ordersItemList = ordersItemService.findOrdersItemByOrdersEmail(orders.getEmail());
		OrdersItem ordersItems = ordersItemList.getFirst();

		// 출력
		System.out.println("{ 배송 대상 주문 목록 테스트 }");

		if (!ordersItems.isCompleted()) {
			System.out.println("배송 완료 상품"
					+ "\n상품명: " + ordersItems.getOrderProductName()
					+ "\n상품 수량: " + ordersItems.getQuantity()
					+ "\n상품 구매 시간: " + ordersItems.getOrderDate());
		} else {
			System.out.println("배송 중인 상품 "
					+ "\n상품명: " + ordersItems.getOrderProductName()
					+ "\n상품 수량: " + ordersItems.getQuantity()
					+ "\n상품 구매 시간: " + ordersItems.getOrderDate());
		}

		// 주문 상품이 존재하는지 확인
		assertThat(ordersItems).isNotNull();

		// 주문시간이 오늘 오후 2시 이후임으로 배송중(true) 상태
		assertThat(ordersItems.isCompleted())
				.isTrue();
	}

	@Test
	@DisplayName("상품 배달중, 배달 완료 확인")
	void deliveryProcessingAndComplete() {

		// 구매자 생성
		Orders orders = ordersService.add("testemail7@testmail.com", "서울시 마포구", 96587);

		// 주문 상품 정보 생성 및 구매 (아메리카노 어제 오후 1시 30분에 주문)
		OrdersItem ordersItemBefore2pm = OrdersItem.builder()
				.orders(orders)
				.orderProductId(1L)
				.orderProductName("아메리카노")
				.quantity(10)
				.orderDate(LocalDateTime.now().minusDays(1).withHour(13).withMinute(30))
				.build();

		// DB에 영속화 및 주문목록에 저장
		ordersItemRepository.save(ordersItemBefore2pm);
		orders.addOrdersItem(ordersItemBefore2pm);

		// 주문 상품 정보 생성 및 구매 (카페라떼 현재 오후 2시 30분에 주문)
		OrdersItem ordersItemAfter2pm = OrdersItem.builder()
				.orders(orders)
				.orderProductId(2L)
				.orderProductName("카페라떼")
				.quantity(1)
				.orderDate(LocalDateTime.now().plusDays(1).withHour(14).plusMinutes(30))
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
		System.out.println("{ 배송 완료 상품 정보 - 아메리카노 (어제 오후 2시 이전 주문) }");
		System.out.println("상품명: " + americano.getOrderProductName());
		System.out.println("상품 수량: " + americano.getQuantity());
		System.out.println("상품 구매 시간: " + americano.getOrderDate());
		System.out.println("배송 상태 (true면 배송중 / false면 배송완료): " + americano.isCompleted());

		// 출력 (배송 중 상품 정보 - 카페라떼)
		System.out.println("\n{ 배송 중 상품 정보 - 카페라떼 (현재 오후 2시 이후 주문) }");
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

	@Test
	@DisplayName("이미 구매한 상품에서 상품의 갯수 차감")
	void modifyReduceProduct() {

		// 주문할 상품명, 이메일, 주소, 수량, 우편주소 하나 저장
		ArrayList<String> productName = new ArrayList<>();
			productName.add("카페라떼");
		String email = "test12@gmail.com";
		String address = "테스트용 주소12";
		ArrayList<Integer> quantity = new ArrayList<>();
			quantity.add(4);
		int postCode = 78454;

		// 주문 하기
		ordersService.startOrders(productName, quantity, email, address, postCode);

		// 주문자 이메일로 주문자 id 획득
		Orders foundOrders = ordersRepository.findByEmail(email).get();
		// 주문자로 주문한 상품 가져오기
		List<OrdersItem> ordersItems = foundOrders.getOrdersItems();

		// 카페라떼 찾아 변수에 저장
		OrdersItem ordersItem = null;

		for (OrdersItem Item : ordersItems) {
			if ("카페라떼".equals(Item.getOrderProductName())) {
				ordersItem = Item;
				break;
			}
		}

		// 찾은 "카페라떼"의 상품 id 획득
		Long orderItemId = ordersItem.getOrdersItemId();
		// 수정할 수량 : 2개
		int reduceQuantity = 2;

		// 수정 작업
		OrdersItem modifyOrdersItem = ordersItemService.save(orderItemId, email, reduceQuantity);

		/// 검증 ///
		// 출력
		System.out.println("{ 수정된 상품 }");
		System.out.println("상품명: " + modifyOrdersItem.getOrderProductName());
		System.out.println("(기존) 상품 갯수: " + quantity.getFirst());
		System.out.println("(수정) 상품 갯수: " + modifyOrdersItem.getQuantity());

		// 존재 확인 및 값 확인
		assertThat(modifyOrdersItem).isNotNull();
		
		// 수정된 주문 상품의 수량이 2개인지 확인
		assertThat(modifyOrdersItem.getQuantity()).isEqualTo(reduceQuantity);

		// DB에서 다시 조회해서 실제 수량이 변경되었는지 확인
		OrdersItem foundOrdersItem = ordersItemRepository.findById(orderItemId).get();
		assertThat(foundOrdersItem).isNotNull();
		assertThat(foundOrdersItem.getQuantity()).isEqualTo(reduceQuantity);

	}

	@Test
	@DisplayName("이미 구매한 상품에서 상품의 갯수 증가")
	void modifyIncreaseProduct() {

		// 주문할 상품명, 이메일, 주소, 수량, 우편주소 하나 저장
		ArrayList<String> productName = new ArrayList<>();
			productName.add("아이스티");
		String email = "test13@gmail.com";
		String address = "테스트용 주소13";
		ArrayList<Integer> quantity = new ArrayList<>();
			quantity.add(5);
		int postCode = 78454;

		// 주문 하기
		ordersService.startOrders(productName, quantity, email, address, postCode);

		// 주문자 이메일로 주문자 id 획득
		Orders foundOrders = ordersRepository.findByEmail(email).get();
		// 주문자로 주문한 상품 가져오기
		List<OrdersItem> ordersItems = foundOrders.getOrdersItems();

		// 카페라떼 찾아 변수에 저장
		OrdersItem ordersItem = null;

		for (OrdersItem Item : ordersItems) {
			if ("아이스티".equals(Item.getOrderProductName())) {
				ordersItem = Item;
				break;
			}
		}

		// 찾은 "카페라떼"의 상품 id 획득
		Long orderItemId = ordersItem.getOrdersItemId();
		// 수정할 수량 : 2개
		int reduceQuantity = 10;

		// 수정 작업
		OrdersItem modifyOrdersItem = ordersItemService.save(orderItemId, email, reduceQuantity);

		/// 검증 ///
		// 출력
		System.out.println("{ 수정된 상품 }");
		System.out.println("상품명: " + modifyOrdersItem.getOrderProductName());
		System.out.println("(기존) 상품 갯수: " + quantity.getFirst());
		System.out.println("(수정) 상품 갯수: " + modifyOrdersItem.getQuantity());

		// 존재 확인 및 값 확인
		assertThat(modifyOrdersItem).isNotNull();

		// 수정된 주문 상품의 수량이 2개인지 확인
		assertThat(modifyOrdersItem.getQuantity()).isEqualTo(reduceQuantity);

		// DB에서 다시 조회해서 실제 수량이 변경되었는지 확인
		OrdersItem foundOrdersItem = ordersItemRepository.findById(orderItemId).get();
		assertThat(foundOrdersItem).isNotNull();
		assertThat(foundOrdersItem.getQuantity()).isEqualTo(reduceQuantity);
	}
}
