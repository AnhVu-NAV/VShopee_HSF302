package com.hsf302.he186049.vshopee.controller;

import com.hsf302.he186049.vshopee.dto.BillViewModel;
import com.hsf302.he186049.vshopee.entity.Bill;
import com.hsf302.he186049.vshopee.repository.BillRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/order")
public class OrderController {

    private final BillRepository billRepository;

    public OrderController(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    @GetMapping("/details/{id}")
    public String orderDetails(@PathVariable("id") Integer id, Model model) {
        Bill bill = billRepository.findById(id).orElse(null);
        if (bill == null) {
            return "redirect:/my-orders?error=notfound";
        }

        String statusClass;
        String statusText;

        switch (bill.getStatus().name()) {
            case "PAID" -> {
                statusClass = "bg-success";
                statusText = "Đã thanh toán";
            }
            case "CANCEL" -> {
                statusClass = "bg-danger";
                statusText = "Đã huỷ";
            }
            default -> {
                statusClass = "bg-warning";
                statusText = "Chờ xác nhận";
            }
        }

        BillViewModel viewModel = new BillViewModel(bill, statusClass, statusText);
        model.addAttribute("order", viewModel);

        double total = bill.getOrder().getOrderDetails().stream()
                .mapToDouble(od -> od.getPrice() * od.getQuantity())
                .sum();

        model.addAttribute("bill", bill);
        model.addAttribute("total", total);
        return "order-details"; // file order-details.html trong /templates
    }
}
