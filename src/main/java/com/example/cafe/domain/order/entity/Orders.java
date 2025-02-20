package com.example.cafe.domain.order.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Orders {

    // 주문 id (기본키)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // id 자동 증가
    private Long orderId;

    // 회원 이메일
    @Column(length = 50, nullable = false) // 길이 50, Not Null 제약
    private String email;

    // 회원 주소
    @Column(length = 50, nullable = false) // 길이 50, Not Null 제약
    private String address;

    // 회원 우편 번호
    @Column
    private int postCode;

    // OrderItem 연관관계 주문한 상품 목록
    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrdersItem> ordersItems = new ArrayList<>();

    // 연관관계 편의 메서드
    public void addOrdersItem(OrdersItem ordersItem) {
        ordersItems.add(ordersItem);
        ordersItem.setOrder(this);
    }
}
