package com.hsf302.he186049.vshopee.controller;

import com.hsf302.he186049.vshopee.entity.Brand;
import com.hsf302.he186049.vshopee.entity.Product;
import com.hsf302.he186049.vshopee.repository.BrandRepository;
import com.hsf302.he186049.vshopee.repository.ProductRepository;
import com.hsf302.he186049.vshopee.service.StorageService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final StorageService storageService;

    public AdminProductController(ProductRepository productRepository, BrandRepository brandRepository, StorageService storageService) {
        this.productRepository = productRepository;
        this.brandRepository = brandRepository;
        this.storageService = storageService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("products", productRepository.findAll());
        return "admin-product-list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("brands", brandRepository.findAll());
        return "admin-product-form";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute Product product, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("brands", brandRepository.findAll());
            return "admin-product-form";
        }
        productRepository.save(product);
        return "redirect:/admin/products";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Integer id, Model model) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) return "redirect:/admin/products";

        model.addAttribute("product", product);
        model.addAttribute("brands", brandRepository.findAll());
        return "admin-product-form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        productRepository.deleteById(id);
        return "redirect:/admin/products";
    }

    @PostMapping("/save")
    public String saveProduct(@ModelAttribute Product product,
                              @RequestParam(required = false) String newBrandName,
                              @RequestParam("imageFile") MultipartFile imageFile,
                              @RequestParam("imageUrl") String imageUrl) throws IOException {
        if (newBrandName != null && !newBrandName.trim().isEmpty()) {
            Brand brand = brandRepository.findByNameIgnoreCase(newBrandName.trim())
                    .orElseGet(() -> brandRepository.save(new Brand(null, newBrandName.trim())));
            product.setBrand(brand);
        }

        // Ưu tiên dùng imageUrl nếu có
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            product.setImage(imageUrl.trim());
        } else if (!imageFile.isEmpty()) {
            String fileName = storageService.saveImage(imageFile); // bạn cần implement saveImage
            product.setImage("/img/" + fileName);
        }
        productRepository.save(product);
        return "redirect:/admin/products";
    }

}
