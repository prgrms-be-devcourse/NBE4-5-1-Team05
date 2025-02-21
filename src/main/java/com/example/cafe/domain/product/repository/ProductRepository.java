package com.example.cafe.domain.product.repository;

import com.example.cafe.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // 찾기
    Optional<Product> findByName(String name);
    Optional<Product> findByProductId(Long id);
    List<Product> findAll();
    Optional<Product> findByPrice(int productPrice);
    Optional<Product> findByNameContaining(String productName);

    // 삭제
    boolean deleteByProductId(Long id);
    boolean deleteByName(String name);
    boolean deleteByPrice(int price);
    boolean deleteByNameContaining(String productName);

    // 수정
    Optional<Product> modifyByProductId(Long id, Product product);
    Optional<Product> modifyByName(String name);
    Optional<Product> modifyByPrice(int price);

    // 갯수 세기
    int countAllProducts();
}
