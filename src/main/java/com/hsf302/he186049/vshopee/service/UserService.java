package com.hsf302.he186049.vshopee.service;

import com.hsf302.he186049.vshopee.entity.User;

public interface UserService {
    User getByUsername(String username);
    void save(User user);
}
