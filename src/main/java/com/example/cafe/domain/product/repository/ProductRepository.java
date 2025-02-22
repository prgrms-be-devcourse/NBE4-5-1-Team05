package com.example.cafe.domain.product.repository;

import com.example.cafe.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // 찾기
    Optional<Product> findByName(String productName);
    Optional<Product> findByProductId(Long id);
    Optional<Product> findByPrice(int productPrice);
    Optional<Product> findByNameContaining(String productName);
    List<Product> findAll();

    // 삭제
    void deleteByProductId(Long id);
    void deleteByName(String name);
    void deleteByPrice(int price);
    void deleteByNameContaining(String productName);

    // 갯수 세기
    long count();
}
