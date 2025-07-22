package com.hsf302.he186049.vshopee.repository;

import com.hsf302.he186049.vshopee.entity.Review;
import com.hsf302.he186049.vshopee.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByProductOrderByCreatedDateDesc(Product product);
}
