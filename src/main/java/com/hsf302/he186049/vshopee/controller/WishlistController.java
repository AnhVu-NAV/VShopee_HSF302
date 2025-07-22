package com.hsf302.he186049.vshopee.controller;

import com.hsf302.he186049.vshopee.entity.Product;
import com.hsf302.he186049.vshopee.entity.User;
import com.hsf302.he186049.vshopee.entity.Wishlist;
import com.hsf302.he186049.vshopee.repository.ProductRepository;
import com.hsf302.he186049.vshopee.repository.UserRepository;
import com.hsf302.he186049.vshopee.repository.WishlistRepository;
import com.hsf302.he186049.vshopee.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/wishlist")
public class WishlistController {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public WishlistController(WishlistRepository wishlistRepository,
                              ProductRepository productRepository,
                              UserRepository userRepository) {
        this.wishlistRepository = wishlistRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/toggle")
    public String toggleWishlist(@RequestParam Integer productId,
                                 @AuthenticationPrincipal CustomUserDetails userDetails) {

        return productRepository.findById(productId)
                .map(product -> {
                    User user = userDetails.getUser();
                    wishlistRepository.findByUserAndProduct(user, product)
                            .ifPresentOrElse(
                                    existing -> wishlistRepository.delete(existing),
                                    () -> {
                                        Wishlist w = new Wishlist();
                                        w.setUser(user);
                                        w.setProduct(product);
                                        wishlistRepository.save(w);
                                    });
                    return "redirect:/product/" + productId;
                })
                .orElse("redirect:/products"); // fallback nếu không tìm thấy product
    }


    @GetMapping
    public String viewWishlist(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        List<Wishlist> wishlist = wishlistRepository.findByUser(userDetails.getUser());
        model.addAttribute("wishlist", wishlist);
        return "wishlist";
    }

    @GetMapping("/remove/{productId}")
    public String removeFromWishlist(@PathVariable Integer productId,
                                     @AuthenticationPrincipal CustomUserDetails userDetails) {
        productRepository.findById(productId).ifPresent(product -> {
            wishlistRepository.deleteByUserAndProduct(userDetails.getUser(), product);
        });
        return "redirect:/wishlist";
    }
}
