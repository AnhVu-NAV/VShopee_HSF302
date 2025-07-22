package com.hsf302.he186049.vshopee.controller;

import com.hsf302.he186049.vshopee.entity.*;
import com.hsf302.he186049.vshopee.enums.BillStatus;
import com.hsf302.he186049.vshopee.repository.*;
import com.hsf302.he186049.vshopee.security.CustomUserDetails;
import com.hsf302.he186049.vshopee.service.*;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final BillRepository billRepository;
    private final MailService mailService;
    private final OtpService otpService;
    private final CartService cartService;
    private final UserService userService;
    private final VietQrService vietQrService;

    public CartController(ProductRepository productRepository,
                          OrderRepository orderRepository,
                          OrderDetailRepository orderDetailRepository,
                          BillRepository billRepository,
                          MailService mailService,
                          OtpService otpService,
                          CartService cartService, UserService userService, VietQrService vietQrService) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.billRepository = billRepository;
        this.mailService = mailService;
        this.otpService = otpService;
        this.cartService = cartService;
        this.userService = userService;
        this.vietQrService = vietQrService;
    }

    private CartSession getCartFromSession(HttpSession session) {
        CartSession sessionCart = (CartSession) session.getAttribute("cart");
        if (sessionCart == null) {
            sessionCart = new CartSession();
            session.setAttribute("cart", sessionCart);
        }
        return sessionCart;
    }

    @GetMapping
    public String viewCart(HttpSession session, @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        CartSession sessionCart = getCartFromSession(session);

        if (userDetails != null && sessionCart.getItems().isEmpty()) {
            cartService.loadCartFromDBToSession(sessionCart, userDetails.getUser());
        }

        model.addAttribute("cartItems", sessionCart.getItems());
        model.addAttribute("total", sessionCart.getTotal());
        return "cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Integer productId,
                            @RequestParam Integer quantity,
                            @AuthenticationPrincipal CustomUserDetails userDetails,
                            HttpSession session) {
        CartSession sessionCart = getCartFromSession(session);
        Product product = productRepository.findById(productId).orElse(null);
        if (product != null && quantity > 0) {
            sessionCart.addItem(product, quantity);
            if (userDetails != null) {
                cartService.syncSessionCartToDB(sessionCart, userDetails.getUser());
            }
        }
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateCart(@RequestParam("productIds") Integer[] productIds,
                             @RequestParam("quantities") Integer[] quantities,
                             @AuthenticationPrincipal CustomUserDetails userDetails,
                             HttpSession session) {
        CartSession sessionCart = getCartFromSession(session);
        for (int i = 0; i < productIds.length; i++) {
            Product product = productRepository.findById(productIds[i]).orElse(null);
            if (product != null && quantities[i] > 0) {
                sessionCart.updateItem(product, quantities[i]);
            }
        }

        if (userDetails != null) {
            cartService.syncSessionCartToDB(sessionCart, userDetails.getUser());
        }

        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeItem(@RequestParam("productId") Integer productId,
                             HttpSession session,
                             @AuthenticationPrincipal CustomUserDetails userDetails) {

        CartSession sessionCart = (CartSession) session.getAttribute("cart");
        if (sessionCart != null) {
            sessionCart.removeItem(productId);
            if (userDetails != null) {
                cartService.syncSessionCartToDB(sessionCart, userDetails.getUser());
            }
        }
        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    public String checkoutPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getByUsername(userDetails.getUsername());
        List<CartItemDB> cartItems = cartService.getItemsDB(user);
        double total = cartItems.stream().mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity()).sum();

        //QR thanh toán
        String qrUrl = vietQrService.createQrCode(total, "Thanh Toan VShopee Don Hang Cua " + user.getFullname());

        model.addAttribute("user", user);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", total);
        model.addAttribute("qrUrl", qrUrl);
        return "checkout-payment";
    }

    @PostMapping("/confirm-payment")
    public String confirmPayment(
            @RequestParam String address,
            @RequestParam String phone,
            @RequestParam String paymentMethod,
            HttpSession session,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userService.getByUsername(userDetails.getUsername());

        if (user.getPhone() == null || user.getPhone().isBlank()) user.setPhone(phone);
        if (user.getAddress() == null || user.getAddress().isBlank()) user.setAddress(address);
        userService.save(user);

        List<CartItemDB> cartItems = cartService.getItemsDB(user);
        if (cartItems == null || cartItems.isEmpty()) {
            session.setAttribute("error", "Giỏ hàng trống!");
            return "redirect:/cart";
        }

        String otp = String.format("%06d", new Random().nextInt(999999));
        session.setAttribute("orderOtp", otp);
        session.setAttribute("otpTime", System.currentTimeMillis());
        session.setAttribute("orderCart", cartItems);
        session.setAttribute("orderPaymentMethod", paymentMethod);

        mailService.sendOtpEmail(user.getEmail(), otp);

        return "redirect:/cart/verify-order-otp";
    }

    @GetMapping("/verify-order-otp")
    public String showOrderOtpPage() {
        return "verify-otp-order";
    }

    @PostMapping("/verify-order-otp")
    public String verifyOrderOtp(@RequestParam("otp") String inputOtp,
                                 HttpSession session,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 RedirectAttributes redirectAttributes) {

        String otp = (String) session.getAttribute("orderOtp");
        Long otpTime = (Long) session.getAttribute("otpTime");

        if (otp == null || otpTime == null || System.currentTimeMillis() - otpTime > 5 * 60 * 1000) {
            redirectAttributes.addFlashAttribute("error", "Mã OTP đã hết hạn!");
            return "redirect:/checkout";
        }

        if (!otp.equals(inputOtp)) {
            redirectAttributes.addFlashAttribute("error", "Mã OTP không đúng!");
            return "redirect:/verify-order-otp";
        }

        User user = userService.getByUsername(userDetails.getUsername());
        List<CartItemDB> cartItems = (List<CartItemDB>) session.getAttribute("orderCart");
        String paymentMethod = (String) session.getAttribute("orderPaymentMethod");

        if (cartItems == null || cartItems.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Đơn hàng không hợp lệ!");
            return "redirect:/cart";
        }

        Order order = new Order();
        order.setUser(user);
        order.setCreatedDate(new Date());
        order = orderRepository.saveAndFlush(order);

        for (CartItemDB item : cartItems) {
            Product product = item.getProduct();
            if (product.getStock() == null || product.getStock() < item.getQuantity()) {
                redirectAttributes.addFlashAttribute("error", "Sản phẩm hết hàng: " + product.getName());
                return "redirect:/cart";
            }

            OrderDetailId odId = new OrderDetailId(product.getId(), order.getId());
            OrderDetail orderDetail = OrderDetail.builder()
                    .id(odId)
                    .order(order)
                    .product(product)
                    .quantity(item.getQuantity())
                    .price(product.getPrice())
                    .build();

            orderDetailRepository.save(orderDetail);

            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
        }

        Bill bill = new Bill();
        bill.setUser(user);
        bill.setOrder(order);
        bill.setStatus(BillStatus.WAIT);
        bill.setCreatedDate(new Date());
        bill.setPaymentMethod(paymentMethod);
        billRepository.save(bill);

        cartService.clearCart(user);
        session.removeAttribute("orderOtp");
        session.removeAttribute("otpTime");
        session.removeAttribute("orderCart");
        session.removeAttribute("orderPaymentMethod");
        session.removeAttribute("cart");


        mailService.sendOrderConfirmation(user.getEmail(), bill);

        session.setAttribute("recentOrderId", order.getId());

        return "redirect:/cart/order-success";
    }

    @GetMapping("/order-success")
    public String showOrderSuccess(HttpSession session, Model model) {
        Integer recentOrderId = (Integer) session.getAttribute("recentOrderId");
        if (recentOrderId != null) {
            model.addAttribute("recentOrderId", recentOrderId);
            session.removeAttribute("recentOrderId");
        }
        return "order-success";
    }

}
