package com.hsf302.he186049.vshopee.repository;

import com.hsf302.he186049.vshopee.entity.Order;
import com.hsf302.he186049.vshopee.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    Optional<Order> findById(Integer id);
}
