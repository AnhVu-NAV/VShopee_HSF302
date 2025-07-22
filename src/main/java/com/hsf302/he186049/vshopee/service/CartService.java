package com.hsf302.he186049.vshopee.service;

import com.hsf302.he186049.vshopee.dto.CartItem;
import com.hsf302.he186049.vshopee.entity.*;

import java.util.List;

public interface CartService {
    void syncSessionCartToDB(CartSession sessionCart, User user);

    void loadCartFromDBToSession(CartSession sessionCart, User user);

    Cart getOrCreateCart(User user);

    void addItem(User user, Integer productId, int quantity);

    void updateItem(User user, Integer productId, int quantity);

    void removeItem(User user, Integer productId);

    void clearCart(User user);

    List<CartItem> getItems(User user);

    List<CartItemDB> getItemsDB(User user);

    public int getTotalItems(User user);
}
