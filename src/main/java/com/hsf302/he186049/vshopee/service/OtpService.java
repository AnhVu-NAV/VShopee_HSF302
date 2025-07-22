package com.hsf302.he186049.vshopee.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Random;

@Service
public class OtpService {

    private final HttpSession session;
    private static final String OTP_KEY = "otp";
    private static final String OTP_TIME = "otp_time";

    public OtpService(HttpSession session) {
        this.session = session;
    }

    public String generateOtp() {
        String otp = String.format("%06d", new Random().nextInt(999999));
        session.setAttribute(OTP_KEY, otp);
        session.setAttribute(OTP_TIME, Instant.now());
        return otp;
    }

    public boolean verifyOtp(String input) {
        String realOtp = (String) session.getAttribute(OTP_KEY);
        Instant createdTime = (Instant) session.getAttribute(OTP_TIME);

        if (realOtp == null || createdTime == null) return false;
        if (Instant.now().isAfter(createdTime.plusSeconds(300))) return false; // quá 5 phút

        return realOtp.equals(input);
    }

    public void clearOtp() {
        session.removeAttribute(OTP_KEY);
        session.removeAttribute(OTP_TIME);
    }
}
