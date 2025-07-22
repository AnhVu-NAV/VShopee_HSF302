package com.hsf302.he186049.vshopee.security;

import com.hsf302.he186049.vshopee.entity.User;
import com.hsf302.he186049.vshopee.security.CustomUserDetails;
import com.hsf302.he186049.vshopee.service.CartService;
import com.hsf302.he186049.vshopee.entity.CartSession;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final CartService cartService;

    public LoginSuccessHandler(CartService cartService) {
        this.cartService = cartService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        // Lấy cartSession từ session (hoặc tạo mới nếu chưa có)
        HttpSession session = request.getSession();
        // 🟢 Lưu user vào session cho chatbot
        session.setAttribute("user", user);

        // Tạo hoặc lấy giỏ hàng từ session
        CartSession cartSession = (CartSession) session.getAttribute("cart");
        if (cartSession == null) {
            cartSession = new CartSession();
            session.setAttribute("cart", cartSession);
        }

        // Load từ DB → session
        CartSession sessionCart = new CartSession();
        cartService.loadCartFromDBToSession(cartSession, user);
        session.setAttribute("cart", sessionCart);

        String role = user.getRole().getName();

        if ("admin".equalsIgnoreCase(role)) {
            response.sendRedirect("/admin");
        } else {
            response.sendRedirect("/products");
        }
    }

}
