package com.hsf302.he186049.vshopee.controller;

import com.hsf302.he186049.vshopee.entity.Bill;
import com.hsf302.he186049.vshopee.enums.BillStatus;
import com.hsf302.he186049.vshopee.repository.BillRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

    private final BillRepository billRepository;

    public AdminOrderController(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("bills", billRepository.findAll());
        return "admin-order-list";
    }

    @PostMapping("/update-status/{id}")
    public String updateStatus(@PathVariable Integer id, @RequestParam("status") BillStatus status) {
        Bill bill = billRepository.findById(id).orElse(null);
        if (bill == null) return "redirect:/admin/orders";

        // Nếu trạng thái hiện tại là DONE, CANCEL, PAID thì không cho cập nhật nữa
        if (bill.getStatus() == BillStatus.DONE || bill.getStatus() == BillStatus.CANCEL || bill.getStatus() == BillStatus.PAID) {
            return "redirect:/admin/orders";
        }

        bill.setStatus(status);
        billRepository.save(bill);
        return "redirect:/admin/orders";
    }

    @GetMapping("/view/{id}")
    public String viewBillDetails(@PathVariable Integer id, Model model) {
        Bill bill = billRepository.findById(id).orElse(null);
        if (bill == null) {
            return "redirect:/admin/orders";
        }

        model.addAttribute("bill", bill);
        return "admin-bill-details"; // ⚠️ Trang Thymeleaf hiển thị chi tiết đơn
    }

}
