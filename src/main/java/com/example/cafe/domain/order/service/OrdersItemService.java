package com.example.cafe.domain.order.service;

import com.example.cafe.domain.order.entity.Orders;
import com.example.cafe.domain.order.entity.OrdersItem;
import com.example.cafe.domain.order.repository.OrdersItemRepository;
import com.example.cafe.domain.order.repository.OrdersRepository;
import com.example.cafe.domain.product.entity.Product;
import com.example.cafe.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class OrdersItemService {
    
    @Autowired
    private final OrdersItemRepository ordersItemRepository;

    @Autowired
    private final ProductService productService;

    @Autowired
    private OrdersRepository ordersRepository;

    /// 기본 메서드 ///
    // 구매내역 id로 구매내역 찾기
    public List<OrdersItem> findByOrdersItemId(Long ordersItemId) {
        OrdersItem ordersItem = ordersItemRepository.findByOrdersItemId(ordersItemId);

        // ordersItem이 비어있지 않다면 반환 / 비어있으면 빈 리스트 반환
        if (ordersItem != null) {
            return List.of(ordersItem);
        } else {
            return List.of();
        }
    }

    // 구매자 이메일로 모든 구매내역 찾기
    public List<OrdersItem> findOrdersItemByOrdersEmail(String ordersEmail) {
        return ordersItemRepository.findOrdersItemByOrdersEmail(ordersEmail);
    }

    // 구매자의 구매내역 시간으로 모든 구매내역 찾기
    public Optional<OrdersItem> findOrdersItemByOrderDate(LocalDateTime orderDate) {
        return ordersItemRepository.findOrdersItemByOrderDate(orderDate);
    }

    // 구매내역의 배송 상태 값으로 찾기
//    public Optional<OrdersItem> findOrdersItemByCompleted(boolean completed) {
//        return ordersItemRepository.findOrdersItemByCompleted(completed);
//    }
    public List<OrdersItem> findOrdersItemByCompleted(boolean completed) {
        try {
            return ordersItemRepository.findAllByCompleted(completed);
        } catch (Exception e) {
            // 예외 처리 및 로깅
            e.printStackTrace(); // 또는 로깅 라이브러리 사용
            return null; // 또는 빈 리스트 반환: return Collections.emptyList();
        }
    }

    // 모든 주문내역 찾기
    public void findAll() {
        ordersItemRepository.findAll();
    }

    // 주문내역 id로 주문내역 삭제
    public void deleteOrdersItemByOrdersItemId(Long orderItemId) {
        OrdersItem ordersItem = ordersItemRepository.findByOrdersItemId(orderItemId);

        if (ordersItem == null) {
            System.out.println("주문내역 id에 해당하는 주문내역이 없습니다.");
        } else if (!ordersItem.isCompleted()) {
            System.out.println("해당 상품은 이미 배송완료가 되어있습니다.");
        } else {
            ordersItemRepository.deleteById(orderItemId);
        }
    }

    // 주문자로 주문내역 삭제
    public void deleteByOrders(Orders orders) {
        ordersItemRepository.deleteByOrders(orders);
    }

    // 주문자 이메일로 주문내역 삭제
    public void deleteByOrdersEmail(String email) {
        Optional<Orders> ordersOp = ordersRepository.findByEmail(email);

        if (ordersOp.isEmpty()) {
            System.out.println("이메일에 해당하는 주문내역이 없습니다.");
        }

        // 주문된 상품 목록 변수에 저장
        Orders orders = ordersOp.get();
        List<OrdersItem> ordersItems = ordersItemRepository.findOrdersItemByOrdersEmail(email);

        if (ordersItems.isEmpty()) {
            System.out.println("주문자 이메일에 해당하는 주문내역이 없습니다.");
        }

        // 배송 중인 상품을 제외한 배송 전인 주문 필터링
        List<OrdersItem> deliveryOrderItems = ordersItems
                .stream()
                .filter(OrdersItem::isCompleted)
                .toList();

        // 주문이 있으면 진행
        if (deliveryOrderItems.isEmpty()) {
            System.out.println("삭제할 수 있는 주문이 없습니다.");
        } else {

            // 연관 관계에서 먼저 제거 후 삭제 진행하기
            for (OrdersItem item : deliveryOrderItems) {
                orders.getOrdersItems().remove(item);
                ordersItemRepository.delete(item);
            }

            // 연관 관계 변경 사항 저장 및 DB 처리
            ordersRepository.save(orders);
            ordersItemRepository.flush();
            System.out.println("배송중인 상품이 정상적으로 삭제되었습니다.");

            // 삭제 후 남은 주문 개수 확인
            List<OrdersItem> remainingOrders = ordersItemRepository.findOrdersItemByOrdersEmail(email);
            System.out.println("삭제 후 남은 주문내역: " + remainingOrders.size());
        }
    }

    // 주문 내역 수정 (수량 수정)
    public OrdersItem save(Long orderItemId, String ordersEmail, int quantity) {

        // 구매자 조회
        Optional<Orders> ordersOp = ordersRepository.findByEmail(ordersEmail);

        // 구매자를 찾지 못했을 경우
        if (ordersOp.isEmpty()) {
            System.out.println("구매자 (" + ordersEmail + ")를 찾을 수 없습니다");
            return null;
        }

        // Orders 객체 추출
        Orders orders = ordersOp.get();

        // 주문 상품 조회 (ID를 이용함)
        Optional<OrdersItem> ordersItemOp = ordersItemRepository.findById(orderItemId);

        // 상품을 찾을 수 없을 경우
        if (ordersItemOp.isEmpty()) {
            System.out.println("주문하신 상품을 찾을 수 없습니다.");
            return null;
        }

        // OrdersItem 객체 추출
        OrdersItem ordersItem = ordersItemOp.get();

        // 주문 상품에 대한 주문자 검증
        if (!ordersItem.getOrders().equals(orders)) {
            System.out.println("주문하신 상품이 해당 주문자의 주문 상품이 아닙니다.");
            return null;
        }

        // 주문내역이 배송 중일 경우
        if (ordersItem.isCompleted()) {
            System.out.println("배송완료된 상품은 수량 수정이 불가능합니다.");
            return null;
        } else {

            // 상품 정보 조회
            Optional<Product> productOp = productService.findByName(ordersItem.getOrderProductName());

            // 조회된 상품이 없을 경우
            if (productOp.isEmpty()) {
                System.out.println("상품 수량을 수정할 수 없습니다.");
                return null;
            }
            Product product = productOp.get();

            // 수정할 상품의 수량에 따라 상품 가격 계산
            int calculatedPrice = product.getPrice() * quantity;

            // 주문 상품 수량 수정 + 가격 수정
            ordersItem.setQuantity(quantity);
            ordersItem.setOrderProductPrice(calculatedPrice);

            // 수정된 주문 상품 저장
            OrdersItem modifiedOrdersItem = ordersItemRepository.save(ordersItem);

            // 수정된 주문 상품을 Optional에 담아 반환
            return modifiedOrdersItem;
        }
    }

    /// 주문 메서드 ///
    // 상품 주문 담기
    public void orderProducts(Orders orders, ArrayList<String> productNames, ArrayList<Integer> quantity) {

        // 들어있는 상품명의 갯수에 따라 단일로 저장
        for (int i = 0; i < productNames.size(); i++) {
            orderProduct(orders, productNames.get(i), quantity.get(i));
        }
    }

    // 단일 상품 주문 담기
    public void orderProduct(Orders orders, String productName, int quantity) {

        // 상품명으로 Product 정보를 조회하여 변수에 저장
        Optional<Product> productOp = productService.findByName(productName);
        Product product = productOp.get();

        // createOrderItem 메서드에 구매자 정보, 상품, 수량을 보내 객체 생성 후 변수에 저장
        OrdersItem orderedItem = createOrderItem(orders, product, quantity);
        orders.addOrdersItem(orderedItem);

        // ordersItemRepository 레포지터리에 저장
        ordersItemRepository.save(orderedItem);
    }

    // 구매자 정보와 구매한 상품들을 객체화시켜 orderProduct로 리턴
    private OrdersItem createOrderItem(Orders orders, Product product, int quantity) {

        // 수량에 따라 상품 가격 변동
        int calculatedPrice = product.getPrice() * quantity;

        // 구매한 상품 객체 생성
        return OrdersItem.builder()
                .orders(orders)
                .orderProductId(product.getProductId())
                .orderProductName(product.getName())
                .orderProductPrice(calculatedPrice)
                .quantity(quantity)
                .build();
    }

    /// 주문 배송 관련 메서드 ///
    // 전날 오후 2시부터 오늘 오후 2시까지의 주문 조회
    @Scheduled(cron = "0 0 14 * * *")
    public void startDelivery() {

        // 배송 상태 업테이트 로직
        updateDeliveryStatus();

        // 배송 처리 로직
        processDelivery();
    }

    // 배송 상태(배송 전인지 배송 중인지) 수정
    public void updateDeliveryStatus() {

        // today2pm는 오늘 오후 2시
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime today2pm = now.withHour(14).withMinute(0).withSecond(0).withNano(0);

        // 배송 중으로 변경할 주문 리스트 (오후 2시 이전에 주문된 상품)
        List<OrdersItem> deliveryStartOrders = ordersItemRepository
                .findAll().stream()
                .filter(ordersItem ->
                        ordersItem.getOrderDate().isBefore(today2pm) && !ordersItem.isCompleted()
                ).toList();

        // 배송 중(true)으로 상태 업데이트
        for (OrdersItem ordersItem : deliveryStartOrders) {
            ordersItem.setCompleted(true);
            ordersItemRepository.save(ordersItem);
        }

        // 확인용 출력
        System.out.println("모든 배송 중 상품: " + deliveryStartOrders.size());
    }

    // 주문 조회 및 배송 처리
    public List<OrdersItem> findOrdersDuring2pm(LocalDateTime startDate, LocalDateTime endDate) {

        // 테스트용
        System.out.println("배송 시작 시간 : " + startDate);
        System.out.println("배송 종료 시간 : " + endDate);

        // ordersItemRepository에서 모든 데이터 조회
        List<OrdersItem> allOrdersItem = ordersItemRepository.findAll();

        // 조회된 OrdersItem 목록 순회하고 OrdersItem의 주문일시가
        // startDate ~ endDate 이전까지인 주문들만 추출
        List<OrdersItem> deliveryOrdersItems = allOrdersItem.stream()
                .filter(ordersItem -> {
                    LocalDateTime orderTime = ordersItem.getOrderDate();
                    return !orderTime.isBefore(startDate)
                            && orderTime.isBefore(endDate);
                }).toList();

        return deliveryOrdersItems;
    }

    // 호출하는 시간 범위 처리
    private void processDelivery() {

        // 배송 전인 주문 목록 가져오기
        List<OrdersItem> deliveryProcessItems = ordersItemRepository.findAll().stream()
                .filter(ordersItem -> !ordersItem.isCompleted())
                .toList();

        // 콘솔 출력용 배송 처리
        if (deliveryProcessItems.isEmpty()) {

            System.out.println("배송할 주문이 없습니다.");

        } else {

            System.out.println("- 배송할 주문 내역 -");

            for (OrdersItem ordersItem : deliveryProcessItems) {
                System.out.println("구매자 이메일: " + ordersItem.getOrders().getEmail());
                System.out.println("<구매 상품 정보>");
                System.out.println("- 상품명: " + ordersItem.getOrderProductName());
                System.out.println("- 갯수: " + ordersItem.getQuantity());
                System.out.println("- 주문 날짜: " + ordersItem.getOrderDate());
                System.out.println("------------------------------");
            }

            System.out.println("구매자의 배송할 총 상품 갯수: " + deliveryProcessItems.size());
        }
    }
}
