package com.hsf302.he186049.vshopee.dto;

import com.hsf302.he186049.vshopee.entity.Bill;

public class BillViewModel {
    private Bill bill;
    private String statusClass;
    private String statusText;

    public BillViewModel() {
    }

    public BillViewModel(Bill bill, String statusClass, String statusText) {
        this.bill = bill;
        this.statusClass = statusClass;
        this.statusText = statusText;
    }

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }

    public String getStatusClass() {
        return statusClass;
    }

    public void setStatusClass(String statusClass) {
        this.statusClass = statusClass;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }
}
