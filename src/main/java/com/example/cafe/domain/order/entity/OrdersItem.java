package com.example.cafe.domain.order.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class OrdersItem {

    // 상품 주문 id (기본키)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // id 자동 증가
    private Long ordersItemId;

    // 주문자 정보 (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_Id")
    private Orders orders;

    // 주문한 상품 (N:1)
    // 객체로 받을 필요가 없다
    //  - 한 주문안에 과도한 product 정보들이 생성이됨
    //  - ex) product 가격등이 반복되어서 저장이 되 버림
    //  - product_id (상품코드)
    //  Product객체가 entity폴더트리내에 있는것이 맞는가?
    //  dto에 가깝다고 생각합니다. 그래서 이걸 알아볼게
    //  데이터 정보 보음 (구조체/VO) 까지만 역할을 하는 객체의 파일트리의 적합한 위치

    // 주문 상품의 id
    @Column(nullable = false)
    private Long orderProductId;

    // 주문한 상품명
    @Column(nullable = false)
    private String orderProductName;

    // 주문한 총가격
    @Column(nullable = false)
    private int orderProductPrice;

    // 주문하려는 상품의 수량
    @Setter
    @Column(nullable = false)
    private int quantity;

    // 임의로 시간을 넣기 위한 메서드
    // 주문 날짜
    @Setter
    @Column
    @Builder.Default
    private LocalDateTime orderDate = LocalDateTime.now();

    // 객체(주문) 수정 날짜
    @LastModifiedDate
    private LocalDateTime modifiedDate;

    // 주문 완료
    @Setter
    @Column(columnDefinition = "boolean default false")
    private boolean completed;

    // 연관관계 편의 메서드
    public void setOrder(Orders orders) {
        this.orders = orders;
    }

}
