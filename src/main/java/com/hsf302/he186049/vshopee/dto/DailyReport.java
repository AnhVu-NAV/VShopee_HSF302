package com.hsf302.he186049.vshopee.dto;

import java.time.LocalDate;

public class DailyReport {
    public LocalDate date;
    public int orderCount;
    public double totalRevenue;

    public DailyReport(LocalDate date, int orderCount, double totalRevenue) {
        this.date = date;
        this.orderCount = orderCount;
        this.totalRevenue = totalRevenue;
    }

    // getters
    public LocalDate getDate() { return date; }
    public int getOrderCount() { return orderCount; }
    public double getTotalRevenue() { return totalRevenue; }
}
