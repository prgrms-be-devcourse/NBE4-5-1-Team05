package com.example.cafe.domain.product.service;

import com.example.cafe.domain.order.service.OrdersService;
import com.example.cafe.domain.product.entity.Product;
import com.example.cafe.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final OrdersService ordersService;

    // 메뉴 담기
    public Product add(String name, int price, String imageURL) {

        Product product = Product.builder()
                .name(name)
                .price(price)
                .imageURL(imageURL)
                .build();

        return productRepository.save(product);
    }

    // 상품 id로 찾기
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    // 상품명으로 찾기
    public Product findByName(String name) {

        Optional<Product> product = productRepository.findByName(name);

        if(product.isEmpty()){
            System.out.println("조회된 상품 없음");
            return null;
        }

        return product.get();
    }
}
