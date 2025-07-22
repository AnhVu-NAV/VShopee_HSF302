package com.hsf302.he186049.vshopee.service.impl;

import com.hsf302.he186049.vshopee.dto.CartItem;

import com.hsf302.he186049.vshopee.entity.*;
import com.hsf302.he186049.vshopee.repository.CartItemRepository;
import com.hsf302.he186049.vshopee.repository.CartRepository;
import com.hsf302.he186049.vshopee.repository.ProductRepository;
import com.hsf302.he186049.vshopee.service.CartService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartServiceImpl(CartRepository cartRepository,
                           CartItemRepository cartItemRepository,
                           ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    @Override
    public Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user).orElseGet(() -> {
            Cart cart = new Cart();
            cart.setUser(user);
            return cartRepository.save(cart);
        });
    }

    @Override
    @Transactional
    public void syncSessionCartToDB(CartSession sessionCart, User user) {
        Cart dbCart = cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });

        // 1. Xóa toàn bộ item cũ trong DB
        dbCart.getItems().clear();

        // 2. Thêm lại các item mới từ session
        for (com.hsf302.he186049.vshopee.entity.CartItem item : sessionCart.getItems()) {
            CartItemDB newItem = new CartItemDB();
            newItem.setCart(dbCart);
            newItem.setProduct(item.getProduct());
            newItem.setQuantity(item.getQuantity());
            dbCart.getItems().add(newItem);
        }

        // 3. Lưu lại cart (cascade = ALL sẽ tự lưu items)
        cartRepository.save(dbCart);
    }


    @Transactional
    @Override
    public void loadCartFromDBToSession(CartSession sessionCart, User user) {
        cartRepository.findByUser(user).ifPresent(dbCart -> {
            sessionCart.clear();
            for (CartItemDB item : dbCart.getItems()) {
                sessionCart.addItem(item.getProduct(), item.getQuantity());
            }
        });
    }

    @Override
    public void addItem(User user, Integer productId, int quantity) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) return;

        Cart cart = getOrCreateCart(user);
        Optional<CartItemDB> existing = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst();

        if (existing.isPresent()) {
            existing.get().setQuantity(existing.get().getQuantity() + quantity);
        } else {
            CartItemDB item = new CartItemDB();
            item.setCart(cart);
            item.setProduct(product);
            item.setQuantity(quantity);
            cart.getItems().add(item);
        }

        cartRepository.save(cart);
    }

    @Override
    public void updateItem(User user, Integer productId, int quantity) {
        Cart cart = getOrCreateCart(user);
        for (CartItemDB item : cart.getItems()) {
            if (item.getProduct().getId().equals(productId)) {
                item.setQuantity(quantity);
                break;
            }
        }
        cartRepository.save(cart);
    }

    @Override
    public void removeItem(User user, Integer productId) {
        Cart cart = getOrCreateCart(user);
        cart.getItems().removeIf(i -> i.getProduct().getId().equals(productId));
        cartRepository.save(cart);
    }

    @Override
    public void clearCart(User user) {
        Cart cart = getOrCreateCart(user);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    @Override
    public List<CartItem> getItems(User user) {
        List<CartItemDB> dbItems = cartItemRepository.findByUser(user);

        return dbItems.stream()
                .map(dbItem -> new CartItem(dbItem.getProduct(), dbItem.getQuantity()))
                .collect(Collectors.toList());
    }

    @Override
    public List<CartItemDB> getItemsDB(User user) {
        return cartItemRepository.findByUser(user);
    }

    @Override
    public int getTotalItems(User user) {
        List<CartItemDB> cartItems = cartItemRepository.findByUser(user);
        return cartItems.stream().mapToInt(CartItemDB::getQuantity).sum();
    }
}

