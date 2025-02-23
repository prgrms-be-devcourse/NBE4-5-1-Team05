package com.example.cafe.domain.product.service;

import com.example.cafe.domain.product.entity.Product;
import com.example.cafe.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    /// 기본 메서드 ///
    // 상품 id로 찾기
    public Optional<Product> findByProductId(Long productId) {

        Optional<Product> product = productRepository.findById(productId);

        if (product.isEmpty()) {
            System.out.println("조회된 상품 없음");
        }

        return product;
    }

    // 상품명으로 찾기
    public Optional<Product> findByName(String name) {

        Optional<Product> product = productRepository.findByName(name);

        if(product.isEmpty()){
            System.out.println("조회된 상품 없음");
            return null;
        }

        return product;
    }

    // 상품 가격으로 찾기
    public Optional<Product> findByPrice(int productPrice) {
        return productRepository.findByPrice(productPrice);
    }

    // 상품 이름에 포함된 단어로 찾기
    public Optional<Product> findByNameContaining(String productName) {
        return productRepository.findByNameContaining(productName);
    }

    // 모든 상품 찾기
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    // 상품 id로 삭제
    public void deleteByProductId(Long id) {
        if (!productRepository.existsById(id)) {
            System.out.println("상품 id에 해당하는 상품이 없습니다.");
        } else {
            productRepository.deleteById(id);
        }
    }

    // 상품 이름으로 삭제
    public void deleteByName(String name) {
        if (productRepository.findByName(name).isEmpty()) {
            System.out.println("상품명에 해당하는 상품이 없습니다.");
        } else {
            productRepository.deleteByName(name);
        }
    }

    // 상품 가격으로 삭제
    public void deleteByPrice(int price) {
        if (productRepository.findByPrice(price).isEmpty()) {
            System.out.println("상품 가격에 해당하는 상품이 없습니다.");
        } else {
            productRepository.deleteByPrice(price);
        }
    }

    // 상품 이름에 특정 단어 포함하면 삭제
    public void deleteByNameContaining(String productName) {
        if (productRepository.findByNameContaining(productName).isEmpty()) {
            System.out.println("특정 단어에 해당하는 상품이 없습니다.");
        } else {
            productRepository.deleteByNameContaining(productName);
        }
    }

    // 상품명을 받아 수정
    public void modifyProduct(Product product, String productName, Integer productPrice, String productImageUrl) {

        // 상품명 수정
        if (productName != null) {
            product.setName(productName);
        }

        // 상품 가격 수정
        if (productPrice != null) {
            product.setPrice(productPrice);
        }

        // 상품 이미지 수정
        if (productImageUrl != null) {
            product.setImageURL(productImageUrl);
        }

        productRepository.save(product);
    }

    /// 기능 메서드 ///
    // 메뉴 담기
    public Product add(String name, int price, String imageURL) {

        Product product = Product.builder()
                .name(name)
                .price(price)
                .imageURL(imageURL)
                .build();

        return productRepository.save(product);
    }

    public Product add(Product product) {

        return productRepository.save(product);
    }
}
