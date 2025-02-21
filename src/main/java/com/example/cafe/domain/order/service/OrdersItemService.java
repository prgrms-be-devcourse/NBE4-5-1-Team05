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

@Service
@RequiredArgsConstructor
public class OrdersItemService {

    @Autowired
    private final OrdersItemRepository ordersItemRepository;

    @Autowired
    private final ProductService productService;

    @Autowired
    private final OrdersRepository ordersRepository;

    // tdd용 시간 변수
    static int ctime;

    public static void setCtime(int ctime) {
        OrdersItemService.ctime = ctime;
    }

    // TODO: orderRepository save가 다중 작업시 반복처리 되는것이 신경쓰인다. jpa가 효율적으로 해주나?
    // TODO: 다중 상품 주문 담기에서 quantity 로직 매커니즘이 올바르지 않다.
    // TODO: Repository 호출이 최소화, 저는 빈번해요 -> jpa 기능 중에 일괄처리 작동 이것이  지혼자 해주는지? 그게 기억이안나요 -> transactional 처리를 해야되요

    // 상품 주문 담기
    public void orderProducts(Orders orders, ArrayList<String> productNames, ArrayList<Integer> quantity) {
        //OrdersItem 수량이 의미하는
        /*
         *각각의 Product에 해당하는 수량이 필요할것 같아요
         * */

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

    // 당일 오후 2시부터 다음날 오후 2시까지의 주문 조회
    @Scheduled(cron = "0 0 14 * * *")
    public void startDelivery() {
        processDelivery();
    }

    // 특정 시간 범위의 주문 조회 및 배송 처리
    public List<Orders> findOrdersDuring2pm() {

        // 현재시간 변수에 저장
        LocalDateTime now = LocalDateTime.now();
        // 기준 시간 오후 2시를 변수에 저장
        LocalDateTime deliveryStartTime = now.withHour(ctime).withMinute(0).withSecond(0);

        // 오후 2시 ~ 다음날 오후 2시 변수 세팅
        LocalDateTime startTime = deliveryStartTime;
//        LocalDateTime endTime = deliveryStartTime.plusMinutes(10);
        LocalDateTime endTime = deliveryStartTime.plusDays(1);

        // 테스트용
        System.out.println("시작 시간 : " + startTime);
        System.out.println("종료 시간 : " + endTime);

        // ordersRepository에서 모든 데이터 조회
        List<Orders> allOrders = ordersRepository.findAll();

        // 조회된 Orders 목록 순회하고 OrdersItem의 주문일시가 오후 2시부터 다음날 오후 2시까지인 주문들 추출
        List<Orders> deliveryOrders = allOrders.stream()
                .filter(orders -> orders.getOrdersItems().stream()
                        .anyMatch(ordersItem -> {
                            LocalDateTime orderTime = ordersItem.getOrderDate();
                            return orderTime.isAfter(startTime) && orderTime.isBefore(endTime);
                        })
                ).toList();

        return deliveryOrders;
    }

    // processDelivery에서 호출하는 시간 범위 처리
    private List<Orders> processDelivery() {

        List<Orders> deliveryOrders = findOrdersDuring2pm();

        // 콘솔 출력용 배송 처리
        if (deliveryOrders.isEmpty()) {
            System.out.println("배송할 주문이 없습니다.");
        } else {
            System.out.println("배송할 주문 내역 (오후 2시 ~ 다음날 오후 2시)");

            for (Orders orders : deliveryOrders) {
                System.out.println("주문자 이메일: " + orders.getEmail());
                System.out.println("<주문 상품 리스트>");

                for (OrdersItem ordersItem : orders.getOrdersItems()) {
                    System.out.println("상품명: " + ordersItem.getOrderProductName() + "갯수: " + ordersItem.getQuantity());
                }
            }
        }

        return deliveryOrders;
    }

}
