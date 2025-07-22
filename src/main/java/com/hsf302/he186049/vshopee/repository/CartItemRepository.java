package com.hsf302.he186049.vshopee.repository;

import com.hsf302.he186049.vshopee.entity.CartItem;
import com.hsf302.he186049.vshopee.entity.CartItemDB;
import com.hsf302.he186049.vshopee.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItemDB, Integer> {
//    List<CartItem> findByUser(User user);

    @Query("SELECT i FROM CartItemDB i WHERE i.cart.user = :user")
    List<CartItemDB> findByUser(@Param("user") User user);

}
