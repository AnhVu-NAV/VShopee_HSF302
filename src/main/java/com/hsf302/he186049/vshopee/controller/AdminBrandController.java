package com.hsf302.he186049.vshopee.controller;

import com.hsf302.he186049.vshopee.entity.Brand;
import com.hsf302.he186049.vshopee.repository.BrandRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/brands")
public class AdminBrandController {

    private final BrandRepository brandRepository;

    public AdminBrandController(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    @GetMapping
    public String viewBrands(Model model) {
        List<Brand> brands = brandRepository.findAll();
        model.addAttribute("brands", brands);
        return "admin-brand";
    }

    @GetMapping("/new")
    public String newBrandForm(Model model) {
        model.addAttribute("brand", new Brand());
        return "admin-brand-form";
    }

    @PostMapping("/save")
    public String saveBrand(@ModelAttribute Brand brand) {
        brandRepository.save(brand);
        return "redirect:/admin/products/new";
    }
}
