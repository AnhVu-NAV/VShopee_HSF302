package com.hsf302.he186049.vshopee.controller;

import com.hsf302.he186049.vshopee.entity.Product;
import com.hsf302.he186049.vshopee.entity.User;
import com.hsf302.he186049.vshopee.repository.*;
import com.hsf302.he186049.vshopee.security.CustomUserDetails;
import com.hsf302.he186049.vshopee.service.CartService;
import com.hsf302.he186049.vshopee.service.ProductService;
import com.hsf302.he186049.vshopee.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class ProductController {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final ReviewRepository reviewRepository;
    private final WishlistRepository wishlistRepository;
    private final ProductService productService;
    private final CartService cartService;
    private final UserService userService;

    public ProductController(ProductRepository productRepository, BrandRepository brandRepository, ReviewRepository reviewRepository, WishlistRepository wishlistRepository, ProductService productService, CartService cartService, UserService userService) {
        this.productRepository = productRepository;
        this.brandRepository = brandRepository;
        this.reviewRepository = reviewRepository;
        this.wishlistRepository = wishlistRepository;
        this.productService = productService;
        this.cartService = cartService;
        this.userService = userService;
    }

    @GetMapping("/products")
    public String listProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) List<Integer> brandIds,
            @RequestParam(required = false) Double min,
            @RequestParam(required = false) Double max,
            @RequestParam(required = false, defaultValue = "price_asc") String sort,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(value = "logoutSuccess", required = false) Boolean logoutSuccess,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model
    ) {
        int pageSize = 9; // 9 sản phẩm mỗi trang

        // Sắp xếp
        Sort sortObj = switch (sort) {
            case "price_desc" -> Sort.by("price").descending();
            case "price_asc" -> Sort.by("price").ascending();
            default -> Sort.by("id").descending();
        };

        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, sortObj);

        // Gọi service hoặc repository để lấy dữ liệu đã lọc & phân trang
        Page<Product> productPage = productService.searchProducts(name, brandIds, min, max, pageRequest);

        // Truyền dữ liệu sang view
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());

        // Để giữ lại filter
        model.addAttribute("name", name);
        model.addAttribute("brandIds", brandIds != null ? brandIds : new ArrayList<>());
        model.addAttribute("min", min);
        model.addAttribute("max", max);
        model.addAttribute("sort", sort);

        if (logoutSuccess != null && logoutSuccess) {
            model.addAttribute("message", "Đăng xuất thành công!");
        }

        // Truyền thêm danh sách thương hiệu nếu cần
        model.addAttribute("brands", brandRepository.findAll());
        if (userDetails != null) {
            User user = userService.getByUsername(userDetails.getUsername());
            int cartCount = cartService.getTotalItems(user);
            model.addAttribute("user", user);
            model.addAttribute("cartCount", cartCount);
        }

        return "product-list";
    }


    @GetMapping("/product/{id}")
    public String viewProduct(@PathVariable Integer id,
                              Model model,
                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            return "redirect:/products?error=notfound";
        }

        Product product = optionalProduct.get();

        // ⚠️ Bắt buộc gọi để tránh lỗi LazyInitializationException nếu reviews là LAZY
        product.getReviews().size();

        model.addAttribute("product", product);
//        System.out.println(product);

        // Thêm wishlist check nếu đã login
        if (userDetails != null) {
            boolean liked = wishlistRepository.existsByUserAndProduct(userDetails.getUser(), product);
            model.addAttribute("liked", liked);
        }

        return "product-detail";
    }


}
