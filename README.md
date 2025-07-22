
# 🛍️ VShopper - Hệ thống bán hàng trực tuyến

VShopper là một ứng dụng web bán hàng được xây dựng với các công nghệ:
- 🔧 Spring Boot (RESTful API)
- 💾 Spring Data JPA & Hibernate
- 🧑‍💻 Thymeleaf (giao diện server-side)
- 🎨 Bootstrap 5 (giao diện responsive)
- 🗄️ MySQL
- 📧 Gửi email xác nhận đơn hàng
- 📊 Báo cáo, thống kê, export PDF/Excel
- 🌐 Đa ngôn ngữ (vi/en) + OTP + Wishlist + Đánh giá sản phẩm...

---

## 🚀 Hướng dẫn cài đặt và chạy

### 1. Yêu cầu hệ thống

- Java 17+
- Maven 3.8+
- MySQL 8.0+
- IDE: IntelliJ IDEA / VSCode / Eclipse

---

### 2. Cài đặt MySQL

Tạo CSDL:

```sql
CREATE DATABASE vshopper CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

---

### 3. Cấu hình `application.properties`

Chỉnh sửa file: `src/main/resources/application.properties`

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/vshopper
spring.datasource.username=your_mysql_user
spring.datasource.password=your_mysql_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Cấu hình mail để gửi OTP/đơn hàng (nếu dùng)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

---

### 4. Chạy dự án

```bash
./mvnw spring-boot:run
```

Truy cập: [http://localhost:8080](http://localhost:8080)

---

### 5. Tài khoản mẫu

| Role | Username | Password |
|------|----------|----------|
| Admin | `admin` | `admin` |
| User  | `user1` | `123456` |

---

## 📚 Các chức năng đã triển khai

Xem [Tài liệu tính năng](vshopper_feature_summary.md) kèm theo.

---

## 🧠 Gợi ý phát triển thêm

- Thanh toán VNPay/MoMo
- Mobile App Flutter
- Tối ưu bảo mật JWT
- CI/CD triển khai tự động

---

## 💻 Tác giả

Phát triển bởi nhóm bạn [2025]
