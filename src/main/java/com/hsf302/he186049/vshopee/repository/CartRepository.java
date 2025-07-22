package com.hsf302.he186049.vshopee.repository;

import com.hsf302.he186049.vshopee.entity.Cart;
import com.hsf302.he186049.vshopee.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    @EntityGraph(attributePaths = "items")
    Optional<Cart> findByUser(User user);
}
