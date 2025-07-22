package com.hsf302.he186049.vshopee.service.impl;



import com.hsf302.he186049.vshopee.service.VietQrService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class VietQrServiceImpl implements VietQrService {

    @Override
    public String createQrCode(double amount, String description) {
        // Định dạng URL dựa trên số tài khoản, ngân hàng và các thông tin khác mà VietQR yêu cầu
        String accountNumber = "9993104999999"; // số tài khoản của bạn
        String bankCode = "970422"; // mã ngân hàng của bạn
        String apiUrl = String.format("https://api.vietqr.io/image/%s-%s-isDSOaU.jpg?amount=%s&description=%s",
                bankCode, accountNumber, amount, description);
        return apiUrl;
    }


    private String callApi(String url, Map<String, Object> params) {
        // Thực hiện gọi API, xử lý mã QR và trả về link URL hình ảnh mã QR
        return "link-to-generated-qr-code";
    }
}
