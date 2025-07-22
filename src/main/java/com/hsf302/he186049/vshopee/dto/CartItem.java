package com.hsf302.he186049.vshopee.dto;

import com.hsf302.he186049.vshopee.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartItem {
    private Product product;
    private int quantity;

    public double getTotal() {
        return product.getPrice() * quantity;
    }
}
