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

    // 주문 상품 찾기
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    // 주문 담기 (샘플)
    public Product add(String name, int price, String imageURL) {

        Product product = Product.builder()
                .name(name)
                .price(price)
                .imageURL(imageURL)
                .build();

        return productRepository.save(product);
    }

//    // 단일 상품 담기
//    public Optional<Product> orderProduct(String name) {
//        Optional<Product> product = productRepository.findByName(name);
//
//        return product;
//    }

    // 단일 상품 담기
    public Optional<Product> orderProduct(String productName, String email, String address, int postCode) {

        // 상품명으로 Product 정보를 조회하여 변수에 저장
        Optional<Product> ordersProduct = productRepository.findByName(productName);

        // OrdersService에서 주문 실행 및 영속화
        ordersService.createOrder(ordersProduct.get() , email, address, postCode);

        return ordersProduct;
    }

//    // 다중 상품 담기
//    public List<Product> ordersProducts(List<String> productNames) {
//
//    }
}
