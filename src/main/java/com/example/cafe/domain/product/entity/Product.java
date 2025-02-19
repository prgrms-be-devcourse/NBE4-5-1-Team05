package com.example.cafe.domain.product.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    // 상품 id (기본키)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // id 자동 증가
    private Long id;

    // 상품명
    @Column(length = 50, nullable = false) // 길이 50, Not Null 제약
    private String name;

    // 상품 가격
    @Column(nullable = false) // Not Null 제약
    private int price;

    // 상품 URL 주소
    @Column(length = 50, nullable = false)
    private String imageURL;
}
