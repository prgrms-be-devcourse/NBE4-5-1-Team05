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
    //여기이게 있으면 순환참조 오류가 떠요 -> private final OrdersService ordersService;
    /*
    네 final 이여가지고
    메모리 로딩 시키려고 OrdersService 객체에 갔는데
    OrdersService는 OrderItemService가 final로 되어있고
    OrderItemService는 ProductService가 final로 되어있어서
    각 entity의 멤버변수를 static 순환 참조되어있어서 누구를 먼저 로딩시킬수 없어서 그렇게 되었어요 맞아요 참조객체(개발자가 만든 객체)사용할때는
    항상 조심해야되요

    1. 설계를 잘한다.
    2. @Lazy를 활용한다(흑마법)
        - Lazy는 초기화를 일단 미루고 나중에 처리하는 것이라서
        - 강제로 순환 참조를 끊어낼수가 있어요ㅋㅋㅋ
        - 10분 고민하고 안되면 바로 말해줘요 넵 빡코딩입니다!!
    */

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
    public void deleteByOrderId(Long id) {
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

    public Product add(Product product) {

        return productRepository.save(product);
    }
}
