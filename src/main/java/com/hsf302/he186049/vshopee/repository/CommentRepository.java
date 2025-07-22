package com.hsf302.he186049.vshopee.repository;

import com.hsf302.he186049.vshopee.entity.Comment;
import com.hsf302.he186049.vshopee.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByProductAndParentIsNullOrderByCreatedAtDesc(Product product);
}
