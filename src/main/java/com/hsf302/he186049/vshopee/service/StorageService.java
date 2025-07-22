package com.hsf302.he186049.vshopee.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class StorageService {

    private static final String UPLOAD_DIR = "src/main/resources/static/img/";

    public String saveImage(MultipartFile file) throws IOException {
        // Đảm bảo thư mục tồn tại
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // Tạo tên file duy nhất
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File destination = new File(UPLOAD_DIR + fileName);

        // Lưu file
        file.transferTo(destination);

        return fileName; // return tên file để lưu vào DB
    }
}
