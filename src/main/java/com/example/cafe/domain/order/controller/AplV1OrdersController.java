package com.example.cafe.domain.order.controller;

import com.example.cafe.domain.order.service.OrdersItemService;
import com.example.cafe.domain.order.service.OrdersService;
import com.example.cafe.domain.product.entity.Product;
import com.example.cafe.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api/v1/hello")
@RequiredArgsConstructor
public class AplV1OrdersController {

    private final OrdersService ordersService;
    private final OrdersItemService ordersItemService;
    private final ProductService productService;

    @GetMapping("/write")
    public String getItems(Model model){
        List<Product> products = productService.findAll(); // 상품 리스트 조회
        model.addAttribute("products", products);
        System.out.println("상품 이미지 URL: " + products.get(1).getImageURL());
        return "domain/order/productList";
    }

    // 상품 정보 조회

}
