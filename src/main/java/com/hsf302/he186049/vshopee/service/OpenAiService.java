package com.hsf302.he186049.vshopee.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.hsf302.he186049.vshopee.entity.*;
import com.hsf302.he186049.vshopee.repository.BillRepository;
import com.hsf302.he186049.vshopee.repository.ProductRepository;
import com.hsf302.he186049.vshopee.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OpenAiService {

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    private final String apiKey = "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"; // Hoặc inject từ @Value như cũ nếu đã hoạt động
    private final ProductRepository productRepository;
    private final CartService cartService;
    private final HttpSession session;
    private final UserRepository userRepository;
    private final BillService billService;
    private final OrderService orderService;
    private final BillRepository billRepository;


    public String chat(String message) {
        RestTemplate restTemplate = new RestTemplate();

        // 1. Lấy danh sách sản phẩm
        List<Product> products = productRepository.findAll();
        String productList = products.stream()
                .map(p -> p.getName() + " - " + p.getDescription())
                .collect(Collectors.joining("\n"));

        // 2. Prompt hệ thống
        String systemPrompt = """
                Bạn là một chatbot hỗ trợ tư vấn mua hàng.
                Danh sách dưới đây là các sản phẩm đang bán trên website. Nếu người dùng muốn mua hay thêm sản phẩm nào vào giỏ hàng, bạn chỉ cần trả lời tên sản phẩm và khẳng định đã thêm.
                Nếu người dùng hỏi về sản phẩm cụ thể thì cung cấp mô tả ngắn gọn, không cần hỏi lại.
                DANH SÁCH SẢN PHẨM:
                """ + productList;

        // 3. Gọi GPT API
        Map<String, Object> requestBody = Map.of(
                "model", "gpt-3.5-turbo",
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", message)
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<JsonNode> response = restTemplate.postForEntity(API_URL, entity, JsonNode.class);

        String reply = response.getBody()
                .get("choices").get(0)
                .get("message").get("content")
                .asText();

        // 4. Tìm sản phẩm trong phản hồi
        String finalReply = reply;
        Optional<Product> matchedProduct = products.stream()
                .filter(p -> finalReply.toLowerCase().contains(p.getName().toLowerCase()))
                .findFirst();

        // 5. Nếu người dùng muốn mua → thêm vào giỏ hàng
        if ((message.toLowerCase().contains("mua") || message.toLowerCase().contains("giỏ"))
                && matchedProduct.isPresent()) {

            Product product = matchedProduct.get();

            User user = (User) session.getAttribute("user");
            if (user != null) {
                CartSession sessionCart = getCartFromSession(session);
                sessionCart.addItem(product, 1);
                cartService.syncSessionCartToDB(sessionCart, user);

                reply += "\n\n✅ Đã thêm sản phẩm **" + product.getName() + "** vào giỏ hàng của bạn.";
            } else {
                reply += "\n\n⚠️ Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng.";
            }
        }

        // 6. Nếu người dùng muốn xem đơn hàng gần đây
        if (message.toLowerCase().contains("đơn hàng gần đây")) {
            User user = (User) session.getAttribute("user");
            List<Bill> bills = billService.getRecentBills(user);
            if (bills.isEmpty()) return "Bạn chưa có đơn hàng nào gần đây.";

            StringBuilder sb = new StringBuilder("📦 Đây là 3 đơn hàng gần đây của bạn:\n");
            for (Bill bill : bills) {
                sb.append("- Mã đơn: ").append(bill.getOrder().getId())
                        .append(" | Ngày: ").append(new SimpleDateFormat("dd/MM/yyyy").format(bill.getCreatedDate()))
                        .append(" | Trạng thái: ").append(bill.getStatus()).append("\n");
            }
            return sb.toString();
        }

        // 7. Tra cứu trạng thái đơn hàng theo mã đơn
        if (message.toLowerCase().matches(".*(mã|đơn)[^0-9]*(\\d+).*")) {
            // Trích xuất mã đơn từ tin nhắn
            Matcher matcher = Pattern.compile(".*?(\\d{1,6}).*").matcher(message);
            if (matcher.find()) {
                int orderId = Integer.parseInt(matcher.group(1));
                Optional<Order> orderOpt = orderService.findById(orderId);

                if (orderOpt.isPresent()) {
                    Order order = orderOpt.get();
                    Optional<Bill> bill = billRepository.findByOrder(order); // cần viết method này

                    return "🔍 Đơn hàng #" + orderId +
                            " được tạo ngày " + new SimpleDateFormat("dd/MM/yyyy").format(bill.get().getCreatedDate()) +
                            " với trạng thái: **" + bill.get().getStatus().name() + "**.";
                } else {
                    return "❌ Không tìm thấy đơn hàng với mã: " + orderId;
                }
            }
        }

        return reply;
    }

    private CartSession getCartFromSession(HttpSession session) {
        CartSession sessionCart = (CartSession) session.getAttribute("cart");
        if (sessionCart == null) {
            sessionCart = new CartSession();
            session.setAttribute("cart", sessionCart);
        }
        return sessionCart;
    }
}
