package com.example.cafe.domain.order.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    // 주문 id (기본키)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // id 자동 증가
    private int orderId;

    // 회원 이메일
    @Column(length = 50, nullable = false) // 길이 50, Not Null 제약
    private String email;

    // 회원 주소
    @Column(length = 50, nullable = false) // 길이 50, Not Null 제약
    private String address;

    // 회원 우편 번호
    private int postCode;
}
