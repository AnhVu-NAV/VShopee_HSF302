package com.hsf302.he186049.vshopee.controller;

import com.hsf302.he186049.vshopee.entity.Product;
import com.hsf302.he186049.vshopee.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/")
    public String home(Model model) {
        List<Product> allProducts = productRepository.findAll();
        Collections.shuffle(allProducts); // xáo trộn ngẫu nhiên

        List<Product> trending = allProducts.stream().limit(4).toList(); // lấy 4 sản phẩm đầu
        model.addAttribute("trendingProducts", trending);

        return "home"; // trả về file home.html
    }
}
