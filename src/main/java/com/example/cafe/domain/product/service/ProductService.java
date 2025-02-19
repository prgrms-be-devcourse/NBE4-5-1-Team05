package com.example.cafe.domain.product.service;

import com.example.cafe.domain.product.entity.Product;
import com.example.cafe.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    // 주문 상품 찾기
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    // 주문 담기
    public Product add(String name, int price) {

        Product product = Product.builder()
                .name(name)
                .price(price)
                .build();

        return productRepository.save(product);
    }
}
