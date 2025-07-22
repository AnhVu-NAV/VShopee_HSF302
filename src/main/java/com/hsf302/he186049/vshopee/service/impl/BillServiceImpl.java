package com.hsf302.he186049.vshopee.service.impl;

import com.hsf302.he186049.vshopee.entity.Bill;
import com.hsf302.he186049.vshopee.entity.User;
import com.hsf302.he186049.vshopee.repository.BillRepository;
import com.hsf302.he186049.vshopee.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BillServiceImpl implements BillService {

    @Autowired
    private BillRepository billRepository;

    @Override
    public List<Bill> getRecentBills(User user) {
        return billRepository.findTop3ByUserOrderByCreatedDateDesc(user);
    }
}
