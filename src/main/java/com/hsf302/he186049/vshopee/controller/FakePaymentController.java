package com.hsf302.he186049.vshopee.controller;

import com.hsf302.he186049.vshopee.entity.Bill;
import com.hsf302.he186049.vshopee.repository.BillRepository;
import com.hsf302.he186049.vshopee.security.CustomUserDetails;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.hsf302.he186049.vshopee.enums.BillStatus;


import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/payment")
public class FakePaymentController {

    private final BillRepository billRepository;

    public FakePaymentController(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    @GetMapping("/fake-gateway")
    public String showFakeGateway(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Optional<Bill> billOpt = billRepository.findFirstByUserOrderByCreatedDateDesc(userDetails.getUser());
        if (billOpt.isEmpty()) return "redirect:/my-orders";
        model.addAttribute("billId", billOpt.get().getId());
        return "fake-gateway";
    }

    @PostMapping("/confirm")
    public String confirmPayment(@RequestParam Integer billId) {
        Bill bill = billRepository.findById(billId).orElse(null);
        if (bill != null) {
            bill.setStatus(BillStatus.PAID);
            billRepository.save(bill);
        }
        return "redirect:/my-orders";
    }
}