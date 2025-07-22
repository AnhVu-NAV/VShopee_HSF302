package com.hsf302.he186049.vshopee.controller;

import com.hsf302.he186049.vshopee.dto.BillViewModel;
import com.hsf302.he186049.vshopee.entity.Bill;
import com.hsf302.he186049.vshopee.repository.BillRepository;
import com.hsf302.he186049.vshopee.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class UserOrderController {

    private final BillRepository billRepository;

    public UserOrderController(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    @GetMapping("/my-orders")
    public String userOrders(Model model,
                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<Bill> bills = billRepository.findByUserOrderByCreatedDateDesc(userDetails.getUser());

        List<BillViewModel> viewModels = bills.stream().map(bill -> {
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

            return new BillViewModel(bill, statusClass, statusText);
        }).collect(Collectors.toList());

        model.addAttribute("orders", viewModels);
        return "my-orders";
    }
}
