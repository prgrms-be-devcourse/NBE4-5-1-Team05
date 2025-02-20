package com.example.cafe.domain.order.entity;

import com.example.cafe.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrdersItem {

    // 주문 상품 id (기본키)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // id 자동 증가
    private Long ordersItemId;

    // 주문자 정보 (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderId")
    private Orders orders;

    // 주문한 상품 (N:1)
    // 객체로 받을 필요가 없다
    //  - 한 주문안에 과도한 product 정보들이 생성이됨
    //  - ex) product 가격등이 반복되어서 저장이 되 버림
    //  - product_id (상품코드)
    //  Product객체가 entity폴더트리내에 있는것이 맞는가?
    //  dto에 가깝다고 생각합니다. 그래서 이걸 알아볼게
    //  데이터 정보 보음 (구조체/VO) 까지만 역할을 하는 객체의 파일트리의 적합한 위치
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private Product product;

    // 주문 총수량
    private int quantity;

    // 주문한 상품 총가격
    private int price;

    // 연관관계 편의 메서드
    public void setOrder(Orders orders) {
        this.orders = orders;
    }
}
