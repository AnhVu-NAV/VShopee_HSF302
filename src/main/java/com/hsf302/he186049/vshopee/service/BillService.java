package com.hsf302.he186049.vshopee.service;

import com.hsf302.he186049.vshopee.entity.Bill;
import com.hsf302.he186049.vshopee.entity.User;

import java.util.List;

public interface BillService {
    List<Bill> getRecentBills(User user);
}
