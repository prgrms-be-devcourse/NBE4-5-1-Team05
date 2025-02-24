package com.example.cafe.domain.order.controller;

import com.example.cafe.domain.order.entity.Orders;
import com.example.cafe.domain.order.service.OrdersItemService;
import com.example.cafe.domain.order.service.OrdersService;
import com.example.cafe.domain.product.entity.Product;
import com.example.cafe.domain.product.service.ProductService;
import lombok.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class AplV1OrdersController {

    private final OrdersService ordersService;
    private final OrdersItemService ordersItemService;
    private final ProductService productService;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderForm {
        private List<OrderItemDto> items; // 장바구니 상품 목록
        private String email;
        private String address;
        private int postCode;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderItemDto {
        private Long productId;
        private String productName;
        private int quantity;
    }

    @GetMapping
    public String getItems(Model model) {
        List<Product> products = productService.findAll(); // 상품 리스트 조회
        model.addAttribute("products", products);
        return "domain/order/productList";
    }

    @PostMapping("/order/form")
    public String orderForm(@ModelAttribute OrderForm orderForm, Model model) {
        System.out.println("✅ Received orderForm: " + orderForm);
        System.out.println("📦 orderForm items: " + orderForm.getItems());
        if (orderForm.getItems() == null || orderForm.getItems().isEmpty()) {
            model.addAttribute("errorMessage", "장바구니가 비어 있습니다.");
            return "redirect:/";
        }
        model.addAttribute("orderForm", orderForm);
        return "domain/order/order-form"; // 주문 정보 입력 페이지 렌더링
    }

    @PostMapping("/order/submit")
    public String submitOrder(@ModelAttribute @Validated OrderForm orderForm, Model model) {
        if (orderForm.getItems() == null || orderForm.getItems().isEmpty()) {
            model.addAttribute("errorMessage", "장바구니가 비어 있습니다.");
            return "redirect:/";
        }

        //  주문 정보 저장 (Orders 테이블)
        Orders orders = ordersService.add(orderForm.getEmail(), orderForm.getAddress(), orderForm.getPostCode());

        //  주문한 상품 저장 (OrdersItem 테이블)
        ArrayList<String> productNames = new ArrayList<>();
        ArrayList<Integer> quantities = new ArrayList<>();

        for (OrderItemDto item : orderForm.getItems()) {
            Optional<Product> productOp = productService.findByProductId(item.getProductId());
            if (productOp.isPresent()) {
                Product product = productOp.get();
                productNames.add(product.getName());
                quantities.add(item.getQuantity());
            }
        }

        ordersItemService.orderProducts(orders, productNames, quantities);

        model.addAttribute("order", orders);
        return "redirect:/order/success"; // 주문 성공 페이지로 이동
    }

    @GetMapping("/order/success")
    public String orderSuccess(Model model) {
        model.addAttribute("message", "🎉 주문이 성공적으로 완료되었습니다!");
        return "domain/order/order-success";
    }
}
