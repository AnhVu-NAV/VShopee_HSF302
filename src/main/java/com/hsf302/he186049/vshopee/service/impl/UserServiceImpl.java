package com.hsf302.he186049.vshopee.service.impl;

import com.hsf302.he186049.vshopee.entity.User;
import com.hsf302.he186049.vshopee.repository.UserRepository;
import com.hsf302.he186049.vshopee.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng: " + username));
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }
}
