package com.hsf302.he186049.vshopee.repository;

import com.hsf302.he186049.vshopee.entity.Bill;
import com.hsf302.he186049.vshopee.entity.Order;
import com.hsf302.he186049.vshopee.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface BillRepository extends JpaRepository<Bill, Integer> {
    List<Bill> findByUserId(Integer userId);

    List<Bill> findAllByCreatedDateBetween(LocalDateTime start, LocalDateTime end);

    Optional<Bill> findFirstByUserOrderByCreatedDateDesc(User user);

    List<Bill> findByUserOrderByCreatedDateDesc(User user);

    List<Bill> findTop3ByUserOrderByCreatedDateDesc(User user);

    Optional<Bill> findByOrder(Order order);
}
