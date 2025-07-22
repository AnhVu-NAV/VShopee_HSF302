package com.hsf302.he186049.vshopee.controller;

import com.hsf302.he186049.vshopee.enums.BillStatus;
import com.hsf302.he186049.vshopee.repository.BillRepository;
import com.hsf302.he186049.vshopee.repository.OrderRepository;
import com.hsf302.he186049.vshopee.repository.ProductRepository;
import com.hsf302.he186049.vshopee.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class AdminController {

    private final BillRepository billRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public AdminController(BillRepository billRepository,
                           ProductRepository productRepository,
                           OrderRepository orderRepository,
                           UserRepository userRepository) {
        this.billRepository = billRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/admin")
    public String dashboard(Model model) {
        double totalRevenue = billRepository.findAll().stream()
                .filter(b -> b.getStatus() == BillStatus.DONE && b.getOrder() != null && b.getOrder().getOrderDetails() != null)
                .flatMapToDouble(b -> b.getOrder().getOrderDetails().stream()
                        .mapToDouble(od -> od.getPrice() * od.getQuantity()))
                .sum();

        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("productCount", productRepository.count());
        model.addAttribute("orderCount", orderRepository.count());
        model.addAttribute("userCount", userRepository.count());

        return "admin-dashboard";
    }
}
