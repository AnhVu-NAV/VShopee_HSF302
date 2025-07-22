package com.hsf302.he186049.vshopee.controller;

import com.hsf302.he186049.vshopee.entity.User;
import com.hsf302.he186049.vshopee.repository.UserRepository;
import com.hsf302.he186049.vshopee.repository.RoleRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public AdminUserController(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "admin-user-list";
    }

    @GetMapping("/ban/{id}")
    public String banUser(@PathVariable Integer id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setBanned(true);
            userRepository.save(user);
        });
        return "redirect:/admin/users";
    }

    @GetMapping("/unban/{id}")
    public String unbanUser(@PathVariable Integer id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setBanned(false);
            userRepository.save(user);
        });
        return "redirect:/admin/users";
    }

    @GetMapping("/role/{id}")
    public String changeRole(@PathVariable Integer id) {
        userRepository.findById(id).ifPresent(user -> {
            var current = user.getRole().getName();
            var newRole = "admin".equals(current) ? "customer" : "admin";
            roleRepository.findByName(newRole).ifPresent(user::setRole);
            userRepository.save(user);
        });
        return "redirect:/admin/users";
    }

    @PostMapping("/toggle-ban")
    public String toggleBan(@RequestParam("userId") Integer userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setBanned(!user.getBanned());
            userRepository.save(user);
        });
        return "redirect:/admin/users";
    }
}
