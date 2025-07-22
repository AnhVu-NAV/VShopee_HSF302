package com.hsf302.he186049.vshopee.service.impl;

import com.hsf302.he186049.vshopee.entity.Product;
import com.hsf302.he186049.vshopee.repository.ProductRepository;
import com.hsf302.he186049.vshopee.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(Integer id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public void saveProduct(Product product) {
        productRepository.save(product);
    }

    @Override
    public void deleteProduct(Integer id) {
        productRepository.deleteById(id);
    }

    public Page<Product> searchProducts(String name, List<Integer> brandIds, Double min, Double max, Pageable pageable) {
        Specification<Product> spec = Specification.where(null);

        if (name != null && !name.trim().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }

        if (brandIds != null && !brandIds.isEmpty()) {
            spec = spec.and((root, query, cb) -> root.get("brand").get("id").in(brandIds));
        }

        if (min != null) {
            spec = spec.and((root, query, cb) -> cb.ge(root.get("price"), min));
        }

        if (max != null) {
            spec = spec.and((root, query, cb) -> cb.le(root.get("price"), max));
        }

        return productRepository.findAll(spec, pageable);
    }
}
