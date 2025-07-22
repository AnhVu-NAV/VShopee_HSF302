package com.hsf302.he186049.vshopee.controller;

import com.hsf302.he186049.vshopee.dto.UserDTO;
import com.hsf302.he186049.vshopee.entity.Role;
import com.hsf302.he186049.vshopee.entity.User;
import com.hsf302.he186049.vshopee.repository.RoleRepository;
import com.hsf302.he186049.vshopee.repository.UserRepository;
import com.hsf302.he186049.vshopee.service.MailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Random;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final MailService emailService;

    public AuthController(UserRepository userRepository, RoleRepository roleRepository, MailService mailService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.emailService = mailService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(@ModelAttribute("user") UserDTO userDTO,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {

        // Kiểm tra email/username trước (tránh gửi OTP nếu đã tồn tại)
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            redirectAttributes.addFlashAttribute("error", "Email đã được sử dụng!");
            return "redirect:/register";
        }
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            redirectAttributes.addFlashAttribute("error", "Tên đăng nhập đã được sử dụng!");
            return "redirect:/register";
        }

        // Sinh mã OTP và lưu session
        String otp = String.format("%06d", new Random().nextInt(999999));
        session.setAttribute("pendingUser", userDTO);
        session.setAttribute("otp", otp);
        session.setAttribute("otpTime", System.currentTimeMillis());

        // Gửi OTP qua email
        emailService.sendOtpEmail(userDTO.getEmail(), otp);

        return "redirect:/verify-otp-register";
    }

    @GetMapping("/verify-otp-register")
    public String showOtpPage(HttpSession session, Model model) {
        UserDTO user = (UserDTO) session.getAttribute("pendingUser");
        if (user != null) {
            model.addAttribute("email", user.getEmail());
        }
        return "verify-otp-register";
    }

    @PostMapping("/verify-otp-register")
    public String verifyOtpRegister(@RequestParam("otp") String inputOtp,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {

        String otp = (String) session.getAttribute("otp");
        Long otpTime = (Long) session.getAttribute("otpTime");

        if (otp == null || otpTime == null || System.currentTimeMillis() - otpTime > 5 * 60 * 1000) {
            redirectAttributes.addFlashAttribute("error", "Mã OTP đã hết hạn!");
            return "redirect:/register";
        }

        if (!otp.equals(inputOtp)) {
            redirectAttributes.addFlashAttribute("error", "Mã OTP không đúng!");
            return "redirect:/verify-otp-register";
        }

        // Lấy user từ session
        UserDTO pendingUser = (UserDTO) session.getAttribute("pendingUser");

        // Kiểm tra lại phòng khi user đã đăng ký trong khi xác minh
        if (userRepository.existsByEmail(pendingUser.getEmail())) {
            redirectAttributes.addFlashAttribute("error", "Email đã được sử dụng!");
            return "redirect:/register";
        }
        if (userRepository.existsByUsername(pendingUser.getUsername())) {
            redirectAttributes.addFlashAttribute("error", "Tên đăng nhập đã được sử dụng!");
            return "redirect:/register";
        }

        // Tạo user
        User user = new User();
        user.setEmail(pendingUser.getEmail());
        user.setFullname(pendingUser.getFullname());
        user.setUsername(pendingUser.getUsername());
        user.setPassword(passwordEncoder.encode(pendingUser.getPassword()));

        Role role = roleRepository.findByName("customer")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy quyền 'customer'"));
        user.setRole(role);

        userRepository.save(user);

        // Gửi mail chào mừng
        emailService.sendWelcomeEmail(user.getEmail(), user.getFullname());

        // Xóa session
        session.removeAttribute("pendingUser");
        session.removeAttribute("otp");
        session.removeAttribute("otpTime");

        redirectAttributes.addFlashAttribute("success", "Đăng ký thành công! Bạn có thể đăng nhập.");
        return "redirect:/login";
    }

    @PostMapping("/resend-otp")
    @ResponseBody
    public String resendOtp(HttpSession session) {
        UserDTO user = (UserDTO) session.getAttribute("pendingUser");
        if (user == null) return "NoSession";

        String newOtp = String.format("%06d", new Random().nextInt(999999));
        session.setAttribute("otp", newOtp);
        session.setAttribute("otpTime", System.currentTimeMillis());

        emailService.sendOtpEmail(user.getEmail(), newOtp);
        return "OK";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/banned")
    public String bannedPage() {
        return "banned"; // trả về banned.html
    }

}
