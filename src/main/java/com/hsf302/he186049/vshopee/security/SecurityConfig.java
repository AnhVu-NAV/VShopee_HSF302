package com.hsf302.he186049.vshopee.security;

import com.hsf302.he186049.vshopee.service.CartService;
import com.hsf302.he186049.vshopee.service.impl.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final CartService cartService;
    private final AuthenticationSuccessHandler loginSuccessHandler;

    public SecurityConfig(CustomUserDetailsService userDetailsService, CartService cartService, AuthenticationSuccessHandler loginSuccessHandler) {
        this.userDetailsService = userDetailsService;
        this.cartService = cartService;
        this.loginSuccessHandler = loginSuccessHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers(
                        "/", "/register", "/verify-otp-register", "/resend-otp",
                        "/css/**", "/js/**", "/images/**", "/img/**", "/static/**",
                        "/products", "/product/**",
                        "/chat", "/chat/**",
                        "/banned",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/swagger-resources/**",
                        "/swagger-ui.html",
                        "/webjars/**"
                ).permitAll()
                .requestMatchers("/admin/**").hasAuthority("admin")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login").permitAll()
                .failureHandler(customFailureHandler())
                .successHandler(loginSuccessHandler)
                .and()
                .logout()
                .logoutSuccessUrl("/products?logoutSuccess=true")
                .permitAll();

        return http.build();
    }


    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(new BCryptPasswordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationFailureHandler customFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }


//    @Bean
//    public AuthenticationSuccessHandler loginSuccessHandler() {
//        return new LoginSuccessHandler(cartService); // Inject cartService vào handler
//    }
}
