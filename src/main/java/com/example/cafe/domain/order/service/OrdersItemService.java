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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrdersItemService {
    
    @Autowired
    private final OrdersItemRepository ordersItemRepository;

    @Autowired
    private final ProductService productService;

    @Autowired
    private OrdersRepository ordersRepository;

    /// 기본 메서드 ///
    // 주문내역 id로 주문내역 찾기
    public Optional<OrdersItem> findByOrdersItemId(Long ordersItemId) {
        return ordersItemRepository.findById(ordersItemId);
    }

    // 구매자로 주문내역 찾기
    public OrdersItem findByOrders(Orders orders) {
        return ordersItemRepository.findByOrders(orders)
                .orElseThrow(() -> new IllegalArgumentException("조회하려는 구매자가 없습니다."));
    }

    // 구매자 이메일로 주문내역 찾기
    public Optional<OrdersItem> findOrdersItemByOrdersEmail(String ordersEmail) {
        return ordersItemRepository.findOrdersItemByOrdersEmail(ordersEmail); // OrdersItemRepository의 findOrdersItemByOrdersEmail 메서드 호출
    }

    // 구매자의 구매내역 시간으로 찾기
    public Optional<OrdersItem> findOrdersItemByOrderDate(LocalDateTime orderDate) {
        return ordersItemRepository.findOrdersItemByOrderDate(orderDate); // OrdersItemRepository의 findOrdersItemByOrderDate 메서드 호출
    }

    // 구매내역의 배송 상태 값으로 찾기
    public Optional<OrdersItem> findOrdersItemByCompleted(boolean completed) {
        return ordersItemRepository.findOrdersItemByCompleted(completed);
    }

    // 모든 주문내역 찾기
    public void findAll() {
        ordersItemRepository.findAll();
    }

    // 주문내역 id로 주문내역 삭제
    public boolean deleteOrdersItemByOrdersItemId(Long orderItemId) {
        if (!ordersItemRepository.existsById(orderItemId)) {
            return false;
        }
        ordersItemRepository.deleteById(orderItemId);

        return true;
    }

    // 주문자로 주문내역 삭제
    public boolean deleteByOrders(Orders orders) {
        if (orders == null || orders.getOrderId() == null) {
            return false;
        }
        ordersItemRepository.deleteByOrders(orders);

        return true;
    }

    // 주문자 이메일로 주문내역 삭제
    public boolean deleteByOrders_Email(String email) {
        Optional<Orders> ordersOp = ordersRepository.findByEmail(email);

        if (ordersOp.isEmpty()) {
            return false;
        }
        ordersItemRepository.deleteByOrders_Email(email);

        return true;
    }

    // 주문 날짜로 주문내역 삭제
    public boolean deleteByOrderDate(LocalDateTime orderDate) {
        if (orderDate == null) {
            return false;
        }
        ordersItemRepository.deleteByOrderDate(orderDate);

        return true;
    }

    // 주문 내역 수정 (수량 수정)
//    public Optional<OrdersItem> save(Long orderItemId, String ordersEmail, int quantity)


    /// 주문 관련 메서드 ///
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
        Product product = productService.findByName(productName);

        // createOrderItem 메서드에 구매자 정보, 상품, 수량을 보내 객체 생성 후 변수에 저장
        OrdersItem orderedItem = createOrderItem(orders, product, quantity);
        orders.addOrdersItem(orderedItem);

        // ordersItemRepository 레포지터리에 저장
        ordersItemRepository.save(orderedItem);
    }

    // 구매자 정보와 구매한 상품들을 객체화시켜 orderProduct로 리턴
    private OrdersItem createOrderItem(Orders orders, Product product, int quantity) {

        // 구매한 상품 객체 생성
        return OrdersItem.builder()
                .orders(orders)
                .orderProductId(product.getProductId())
                .orderProductName(product.getName())
                .orderProductPrice(product.getPrice())
                .quantity(quantity)
                .build();
    }

    /// 주문 배송 관련 메서드 ///
    // 당일 오후 2시부터 다음날 오후 2시까지의 주문 조회
    @Scheduled(cron = "0 0 14 * * *")
    public void startDelivery() {

        // 배송 상태 업테이트 로직
        updateDeliveryStatus();

        // 배송 처리 로직
        processDelivery();
    }

    // 배송 상태(배송완료인지 배송중인지) 수정
    public void updateDeliveryStatus() {

        LocalDateTime endDate = LocalDateTime.now().withHour(14).withMinute(0).withSecond(0);
        LocalDateTime startDate = endDate.minusDays(1).withHour(14).withMinute(0).withSecond(0);

        // 전날 오후 2시 이후, 오늘 오후 2시 이전의 주문 담기
        List<OrdersItem> deliveryProcessOrders = ordersItemRepository
                .findAll().stream()
                .filter(ordersItem -> {
                    LocalDateTime orderTime = ordersItem.getOrderDate();
                    return !orderTime.isBefore(startDate)
                            && !orderTime.isAfter(endDate);
                }).toList();

        // 어제 오후 2시 이전 주문 변수에 담기
        List<OrdersItem> deliveryCompleteOrders = ordersItemRepository
                .findAll().stream()
                .filter(ordersItem -> {
                    LocalDateTime orderTime = ordersItem.getOrderDate();
                    return orderTime.isBefore(startDate);
                }).toList();

        // 오후 2시 이후 주문은 배송중(true)로 수정
        for (OrdersItem ordersItem : deliveryProcessOrders) {
            ordersItem.setCompleted(true);
            ordersItemRepository.save(ordersItem);
        }

        // 오후 2시 이전 주문은 배송완료(false)로 수정
        for (OrdersItem ordersItem : deliveryCompleteOrders) {
            ordersItem.setCompleted(false);
            ordersItemRepository.save(ordersItem);
        }

        // 확인용 출력
        System.out.println("모든 배송중 상품: " + deliveryProcessOrders.size());
        System.out.println("모든 배송완료 상품: " + deliveryCompleteOrders.size());
    }

    // 호출하는 시간 범위 처리
    private void processDelivery() {

        // 배송 주문 목록 가져오기
        List<OrdersItem> deliveryProcessItems = findOrdersDuring2pm();

        // 콘솔 출력용 배송 처리
        if (deliveryProcessItems.isEmpty()) {

            System.out.println("배송할 주문이 없습니다.");

        } else {

            System.out.println("- 배송할 주문 내역 (어제 오후 2시 ~ 오늘 오후 2시) -");

            for (OrdersItem ordersItem : deliveryProcessItems) {
                System.out.println("------------------------------");
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

    // 전날 오후 2시 ~ 오늘 오후 2시를 기준으로 주문 조회 및 배송 처리
    public List<OrdersItem> findOrdersDuring2pm() {

        // 현재시간 변수에 저장
        LocalDateTime now = LocalDateTime.now();

        // 기준 시간 오후 2시 ~ 다음날 오후 2시 변수 세팅
        // endTime은 오늘 오후 2시 / startTime은 어제 오후 2시
        LocalDateTime endTime = now.withHour(14).withMinute(0).withSecond(0);
        LocalDateTime startTime = endTime.minusDays(1).withHour(14).withMinute(0).withSecond(0);

        // 테스트용
        System.out.println("어제 오후 2시 : " + startTime);
        System.out.println("오늘 오후 2시 : " + endTime);

        // ordersItemRepository에서 모든 데이터 조회
        List<OrdersItem> allOrdersItem = ordersItemRepository.findAll();

        // 조회된 OrdersItem 목록 순회하고 OrdersItem의 주문일시가
        // 어제 오후 2시 이후부터 오늘 오후 2시까지인 주문들만 추출
        List<OrdersItem> deliveryOrdersItems = allOrdersItem.stream()
                .filter(ordersItem -> {
                    LocalDateTime orderTime = ordersItem.getOrderDate();
                    return !orderTime.isBefore(startTime)
                            && orderTime.isBefore(endTime);
                }).toList();

        return deliveryOrdersItems;
    }
}
