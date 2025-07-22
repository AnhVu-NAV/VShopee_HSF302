package com.hsf302.he186049.vshopee.controller;

import com.hsf302.he186049.vshopee.dto.DailyReport;
import com.hsf302.he186049.vshopee.entity.Bill;
import com.hsf302.he186049.vshopee.repository.BillRepository;
import com.hsf302.he186049.vshopee.repository.OrderDetailRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.hsf302.he186049.vshopee.enums.BillStatus;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class AdminReportController {

    private final BillRepository billRepository;
    private final OrderDetailRepository orderDetailRepository;

    public AdminReportController(BillRepository billRepository, OrderDetailRepository orderDetailRepository) {
        this.billRepository = billRepository;
        this.orderDetailRepository = orderDetailRepository;
    }

    @GetMapping("/admin/report")
    public String report(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) String month,
            Model model
    ) {
        List<Bill> bills;

        // Lọc theo ngày
        if (fromDate != null && toDate != null) {
            bills = billRepository.findAllByCreatedDateBetween(
                    fromDate.atStartOfDay(),
                    toDate.plusDays(1).atStartOfDay()
            );
        }
        // Lọc theo tháng
        else if (month != null && !month.isEmpty()) {
            YearMonth ym = YearMonth.parse(month);
            LocalDateTime start = ym.atDay(1).atStartOfDay();
            LocalDateTime end = ym.atEndOfMonth().plusDays(1).atStartOfDay();
            bills = billRepository.findAllByCreatedDateBetween(start, end);
        }
        // Không lọc
        else {
            bills = billRepository.findAll();
        }

        // Chỉ lấy các bill đã DONE
        bills = bills.stream()
                .filter(b -> b.getStatus() == BillStatus.DONE)
                .toList();

        // Gom doanh thu theo ngày
        Map<LocalDate, List<Bill>> grouped = bills.stream()
                .collect(Collectors.groupingBy(b -> convertToLocalDate(b.getCreatedDate()), TreeMap::new, Collectors.toList()));

        List<DailyReport> dailyReports = new ArrayList<>();
        double totalRevenue = 0;

        for (var entry : grouped.entrySet()) {
            LocalDate date = entry.getKey();
            List<Bill> dayBills = entry.getValue();

            int orderCount = dayBills.size();
            double dayTotal = dayBills.stream()
                    .mapToDouble(b -> b.getOrder().getOrderDetails().stream()
                            .mapToDouble(od -> od.getPrice() * od.getQuantity())
                            .sum())
                    .sum();

            totalRevenue += dayTotal;
            dailyReports.add(new DailyReport(date, orderCount, dayTotal));
        }

        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        model.addAttribute("month", month);
        model.addAttribute("dailyReports", dailyReports);
        model.addAttribute("totalRevenue", totalRevenue);

        return "admin-report";
    }

    private LocalDate convertToLocalDate(Date date) {
        if (date instanceof java.sql.Date sqlDate) {
            return sqlDate.toLocalDate();
        } else {
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
    }

}
