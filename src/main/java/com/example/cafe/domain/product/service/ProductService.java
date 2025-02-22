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
        return productRepository.findById(productId);
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

    // 모든 상품 찾기
    List<Product> findAll() {
        return productRepository.findAll();
    }

    // 상품 가격으로 찾기
    public Optional<Product> findByPrice(int productPrice) {
        return productRepository.findByPrice(productPrice);
    }

    // 상품 이름에 포함된 단어로 찾기
    public Optional<Product> findByNameContaining(String productName) {
        return productRepository.findByNameContaining(productName);
    }

    // 상품 id로 삭제
    public boolean deleteByOrderId(Long id) {
        if (!productRepository.existsById(id)) {
            return false;
        }
        productRepository.deleteById(id);

        return true;
    }

    // 상품 이름으로 삭제
    public boolean deleteByName(String name) {
        if (productRepository.findByName(name).isEmpty()) {
            return false;
        }
        productRepository.deleteByName(name);

        return true;
    }

    // 상품 가격으로 삭제
    public boolean deleteByPrice(int price) {
        if (productRepository.findByPrice(price).isEmpty()) {
            return false;
        }
        productRepository.deleteByPrice(price);

        return true;
    }

    // 상품 이름에 특정 단어 포함하면 삭제
    public boolean deleteByNameContaining(String productName) {
        if (productRepository.findByNameContaining(productName).isEmpty()) {
            return false;
        }
        productRepository.deleteByNameContaining(productName);

        return true;
    }

    // 수정 (상품명, 상품가격, 상품 이미지)

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
}
