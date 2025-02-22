package com.example.cafe.global.controller;

import com.example.cafe.domain.product.entity.Product;
import com.example.cafe.domain.product.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
        return "admin"; // admin.html 템플릿 반환
    }
}
