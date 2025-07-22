package com.hsf302.he186049.vshopee.service;

import com.hsf302.he186049.vshopee.entity.Bill;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final JavaMailSender mailSender;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOrderConfirmation(String to, Bill bill) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject("Xác nhận đơn hàng #" + bill.getId());

            StringBuilder content = new StringBuilder();
            content.append("<h2>Xin chào ").append(bill.getUser().getUsername()).append(",</h2>");
            content.append("<p>Cảm ơn bạn đã đặt hàng tại vshoppee.</p>");
            content.append("<p>Phương thức thanh toán: <b>").append(bill.getPaymentMethod()).append("</b></p>");
            content.append("<p>Trạng thái đơn hàng: <b>").append(bill.getStatus()).append("</b></p>");
            content.append("<p>Mã đơn hàng: <b>").append(bill.getId()).append("</b></p>");

            helper.setText(content.toString(), true);
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendSimpleEmail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false);
            helper.setFrom("anhvucp6@gmail.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, false); // false = text/plain, không HTML
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendOtpEmail(String to, String otp) {
        String subject = "Xác minh tài khoản - VShopper";
        String body = "<p>Chào bạn,</p><p>Mã OTP của bạn là: <strong>" + otp + "</strong></p><p>Mã có hiệu lực trong 5 phút.</p>";
        sendHtmlEmail(to, subject, body);
    }

    public void sendWelcomeEmail(String to, String name) {
        String subject = "Chào mừng đến với VShopper!";
        String body = "<p>Xin chào <strong>" + name + "</strong>,</p><p>Bạn đã đăng ký thành công tài khoản tại VShopper.</p><p>Chúc bạn mua sắm vui vẻ!</p>";
        sendHtmlEmail(to, subject, body);
    }

    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("anhvucp6@gmail.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = HTML
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


}
