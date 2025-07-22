package com.hsf302.he186049.vshopee.service;

import com.hsf302.he186049.vshopee.entity.Order;
import com.hsf302.he186049.vshopee.entity.User;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    Optional<Order> findById(int id);
}
