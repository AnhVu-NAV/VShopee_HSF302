package com.hsf302.he186049.vshopee.service.impl;

import com.hsf302.he186049.vshopee.entity.Order;
import com.hsf302.he186049.vshopee.entity.User;
import com.hsf302.he186049.vshopee.repository.OrderRepository;
import com.hsf302.he186049.vshopee.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public Optional<Order> findById(int id) {
        return orderRepository.findById(id);
    }
}
