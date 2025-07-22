package com.hsf302.he186049.vshopee.controller;

import com.hsf302.he186049.vshopee.entity.Review;
import com.hsf302.he186049.vshopee.repository.ReviewRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/reviews")
public class AdminReviewController {

    private final ReviewRepository reviewRepository;

    public AdminReviewController(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @GetMapping
    public String listReviews(Model model) {
        model.addAttribute("reviews", reviewRepository.findAll());
        return "admin-review";
    }

    @PostMapping("/delete/{id}")
    public String deleteReview(@PathVariable("id") Integer id) {
        reviewRepository.deleteById(id);
        return "redirect:/admin/reviews";
    }

}
