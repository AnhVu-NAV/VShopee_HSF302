package com.hsf302.he186049.vshopee.repository;

import com.hsf302.he186049.vshopee.entity.Wishlist;
import com.hsf302.he186049.vshopee.entity.Product;
import com.hsf302.he186049.vshopee.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Integer> {
    List<Wishlist> findByUser(User user);

    Optional<Wishlist> findByUserAndProduct(User user, Product product);

    void deleteByUserAndProduct(User user, Product product);

    boolean existsByUserAndProduct(User user, Product product);
}
