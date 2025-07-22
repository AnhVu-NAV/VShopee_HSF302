package com.hsf302.he186049.vshopee.controller;

import com.hsf302.he186049.vshopee.entity.Comment;
import com.hsf302.he186049.vshopee.entity.Product;
import com.hsf302.he186049.vshopee.repository.CommentRepository;
import com.hsf302.he186049.vshopee.repository.ProductRepository;
import com.hsf302.he186049.vshopee.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Controller
@RequestMapping("/comments")
public class CommentController {

    private final CommentRepository commentRepository;
    private final ProductRepository productRepository;

    public CommentController(CommentRepository commentRepository, ProductRepository productRepository) {
        this.commentRepository = commentRepository;
        this.productRepository = productRepository;
    }

    @PostMapping("/add")
    public String addComment(@RequestParam Integer productId,
                             @RequestParam String content,
                             @RequestParam(required = false) Integer parentId,
                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) return "redirect:/products";

        Comment comment = new Comment();
        comment.setUser(userDetails.getUser());
        comment.setProduct(product);
        comment.setContent(content);
        comment.setCreatedAt(new Date());

        if (parentId != null) {
            comment.setParent(commentRepository.findById(parentId).orElse(null));
        }

        commentRepository.save(comment);
        return "redirect:/product/" + productId;
    }
}
