package com.hsf302.he186049.vshopee.controller;

import com.hsf302.he186049.vshopee.entity.Product;
import com.hsf302.he186049.vshopee.entity.Review;
import com.hsf302.he186049.vshopee.repository.ProductRepository;
import com.hsf302.he186049.vshopee.repository.ReviewRepository;
import com.hsf302.he186049.vshopee.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/review")
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    public ReviewController(ReviewRepository reviewRepository, ProductRepository productRepository) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
    }

    @PostMapping("/submit")
    public String submitReview(@RequestParam Integer productId,
                               @RequestParam int rating,
                               @RequestParam String content,
                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (content == null || content.trim().isEmpty()) {
            return "redirect:/product/" + productId + "?error=emptyContent";
        }

        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return "redirect:/products";
        }

        Review review = new Review();
        review.setProduct(product);
        review.setUser(userDetails.getUser());
        review.setRating(rating);
        review.setContent(content);
        review.setCreatedDate(new Date());

        reviewRepository.save(review);
        return "redirect:/product/" + productId + "?success=reviewed";
    }

}
