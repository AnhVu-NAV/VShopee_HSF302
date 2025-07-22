package com.hsf302.he186049.vshopee.controller;

import com.hsf302.he186049.vshopee.entity.Bill;
import com.hsf302.he186049.vshopee.repository.BillRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.time.*;
import java.util.List;

@Controller
@RequestMapping("/admin/export")
public class ExcelExportController {

    private final BillRepository billRepository;

    public ExcelExportController(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    @GetMapping("/excel")
    public void exportExcelByDateOrMonth(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String month, // yyyy-MM
            HttpServletResponse response) throws IOException {

        List<Bill> bills;

        if (date != null) {
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = start.plusDays(1);
            bills = billRepository.findAllByCreatedDateBetween(start, end);
        } else if (month != null) {
            YearMonth ym = YearMonth.parse(month);
            LocalDateTime start = ym.atDay(1).atStartOfDay();
            LocalDateTime end = ym.atEndOfMonth().plusDays(1).atStartOfDay();
            bills = billRepository.findAllByCreatedDateBetween(start, end);
        } else {
            bills = billRepository.findAll();
        }

        // Tạo file Excel
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Orders");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Mã đơn");
        header.createCell(1).setCellValue("Khách hàng");
        header.createCell(2).setCellValue("Email");
        header.createCell(3).setCellValue("Ngày đặt");
        header.createCell(4).setCellValue("Trạng thái");
        header.createCell(5).setCellValue("Tổng tiền");

        int rowNum = 1;
        for (Bill bill : bills) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(bill.getId());
            row.createCell(1).setCellValue(bill.getUser().getFullname());
            row.createCell(2).setCellValue(bill.getUser().getEmail());
            row.createCell(3).setCellValue(bill.getCreatedDate().toString());
            row.createCell(4).setCellValue(bill.getStatus().toString());

            double total = bill.getOrder().getOrderDetails().stream()
                    .mapToDouble(od -> od.getPrice() * od.getQuantity())
                    .sum();
            row.createCell(5).setCellValue(total);
        }

        // Xuất file
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=orders.xlsx");

        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
