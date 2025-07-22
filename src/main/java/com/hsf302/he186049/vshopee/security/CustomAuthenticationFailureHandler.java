package com.hsf302.he186049.vshopee.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {

        if (exception instanceof LockedException) {
            // Tài khoản bị banned → chuyển sang trang /banned
            response.sendRedirect("/banned");
        } else {
            // Lỗi đăng nhập khác → về lại login với param lỗi
            response.sendRedirect("/login?error");
        }
    }
}
