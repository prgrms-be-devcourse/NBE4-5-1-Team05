package com.example.cafe.global.controller;

import com.example.cafe.domain.product.entity.Product;
import com.example.cafe.domain.product.service.ProductService;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProductService productService;

    public AdminController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String adminPage(Model model) {
        List<Product> products = productService.findAll(); // 전체 상품 리스트 조회
        model.addAttribute("products", products);
        return "domain/order/admin"; // admin.html 템플릿 반환
    }

    @GetMapping("/add")
    public String addProduct(Model model) {
        // 전체 상품 리스트 조회
        model.addAttribute("product",new Product());
        return "domain/order/admin-add"; // admin.html 템플릿 반환
    }

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<String> addProduct(
            @NotBlank(message = "상품명을 입력하세요.") @Length(min = 3, message = "상품명은 최소 3자 이상이어야 합니다.") @RequestParam String name,
            @RequestParam(defaultValue = "0") int price, // 기본값 설정
            @NotBlank(message = "이미지 URL을 입력하세요.") @RequestParam String imageURL
    ) {
        // 가격 검증
        if (price < 1) {
            return ResponseEntity.badRequest().body("가격은 1원 이상이어야 합니다.");
        }

        // 상품 저장 로직
        productService.add(name, price, imageURL);

        return ResponseEntity.ok("상품이 추가되었습니다.");
    }
}
