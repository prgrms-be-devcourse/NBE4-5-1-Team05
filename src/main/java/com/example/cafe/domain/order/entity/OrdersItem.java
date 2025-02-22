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
