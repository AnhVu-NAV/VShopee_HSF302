package com.hsf302.he186049.vshopee.service;

import com.hsf302.he186049.vshopee.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    List<Product> getAllProducts();
    Product getProductById(Integer id);
    void saveProduct(Product product);
    void deleteProduct(Integer id);
    Page<Product> searchProducts(String name, List<Integer> brandIds, Double min, Double max, Pageable pageable);

}
