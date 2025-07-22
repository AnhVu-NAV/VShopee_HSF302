package com.hsf302.he186049.vshopee.controller;

import com.hsf302.he186049.vshopee.entity.Bill;
import com.hsf302.he186049.vshopee.repository.BillRepository;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Controller
@RequestMapping("/admin/export")
public class PdfExportController {

    private final BillRepository billRepository;

    public PdfExportController(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    @GetMapping("/pdf")
    public void exportToPDF(
            HttpServletResponse response,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String month // định dạng: yyyy-MM
    ) throws IOException {

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=orders.pdf");

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

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Paragraph title = new Paragraph("Danh sách đơn hàng", titleFont);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100f);
        writeTableHeader(table);
        writeTableData(table, bills);

        document.add(table);
        document.close();
    }

    private void writeTableHeader(PdfPTable table) {
        String[] headers = {"Bill ID", "User", "Total", "Payment", "Status", "Date"};
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(Color.LIGHT_GRAY);
            table.addCell(cell);
        }
    }

    private void writeTableData(PdfPTable table, List<Bill> bills) {
        for (Bill bill : bills) {
            double total = bill.getOrder().getOrderDetails().stream()
                    .mapToDouble(od -> od.getPrice() * od.getQuantity())
                    .sum();
            table.addCell(String.valueOf(bill.getId()));
            table.addCell(bill.getUser().getUsername());
            table.addCell(String.format("%.2f", total));
            table.addCell(bill.getPaymentMethod());
            table.addCell(bill.getStatus().toString());
            table.addCell(bill.getCreatedDate().toString());
        }
    }
}
