package com.example.cafe.global.controller;

import com.example.cafe.domain.product.entity.Product;
import com.example.cafe.domain.product.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProductService productService;

    public AdminController(ProductService productService) {
        this.productService = productService;
    }
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductForm {
        @NotBlank(message = "상품명을 입력하세요.")
        @Length(min = 3, message = "상품명은 최소 3자 이상이어야 합니다.")
        private String name;

        @Min(value = 1, message = "가격은 1원 이상이어야 합니다.")
        private int price;

        @NotBlank(message = "이미지 URL을 입력하세요.")
        private String imageURL;
    }


    @GetMapping
    public String adminPage(Model model) {
        List<Product> products = productService.findAll(); // 전체 상품 리스트 조회
        model.addAttribute("products", products);
        return "domain/order/admin"; // admin.html 템플릿 반환
    }

    @GetMapping("/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new ProductForm()); // 빈 ProductForm 객체 전달
        return "domain/order/admin-add";
    }


    @PostMapping("/add")
    public String addProduct(
            @Valid @ModelAttribute("product") ProductForm form,
            BindingResult bindingResult,
            Model model
    ) {
        System.out.println("Received name: " + form.getName());
        System.out.println("Received price: " + form.getPrice());
        System.out.println("Received imageURL: " + form.getImageURL());

        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", bindingResult.getFieldError().getDefaultMessage());
            return "domain/order/admin-add"; // 상품 추가 폼 유지
        }

        System.out.println("2"+form.getPrice());
        if (form.getPrice() < 1) {
            model.addAttribute("errorMessage", "가격은 1원 이상이어야 합니다.");
            return "domain/order/admin-add"; // 상품 추가 폼 유지
        }
        System.out.println("3"+form.getPrice());
        productService.add(form.getName(), form.getPrice(), form.getImageURL());

        model.addAttribute("successMessage", "상품이 성공적으로 추가되었습니다!");
        return "domain/order/admin-success";
    }

    @GetMapping("/edit/{id}")
    public String showEditProductForm(@PathVariable Long id, Model model) {
        Optional<Product> productOptional = productService.findByProductId(id);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            ProductForm form = new ProductForm(product.getName(), product.getPrice(), product.getImageURL());
            model.addAttribute("product", form);
            model.addAttribute("productId", id); // 상품 ID 전달
            return "domain/order/admin-edit";
        } else {
            model.addAttribute("errorMessage", "해당 상품을 찾을 수 없습니다.");
            return "redirect:/admin";
        }
    }

    @PostMapping("/edit/{id}")
    public String editProduct(
            @PathVariable Long id,
            @Valid @ModelAttribute("product") ProductForm form,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", bindingResult.getFieldError().getDefaultMessage());
            model.addAttribute("productId", id); // 수정 페이지 유지
            return "domain/order/admin-edit";
        }

        productService.modifyProduct(id, form.getName(), form.getPrice(), form.getImageURL());
        return "redirect:/admin";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteByProductId(id);
        return "redirect:/admin";
    }
}
