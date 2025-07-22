# 📘 Tài liệu kỹ thuật - Danh sách tính năng hệ thống VShopper

| STT | Tên chức năng | Đường dẫn | Quyền | Class liên quan | Giao diện |
|-----|----------------|------------|--------|-------------------|------------|
| 1 | Đăng ký / Đăng nhập | /register, /login | Public | AuthController, User | register.html, login.html |
| 2 | Quản lý sản phẩm | /admin/products | Admin | Product, ProductController, ProductRepository | admin-product-list.html, admin-product-form.html |
| 3 | Xem sản phẩm | /products, /product/{id} | Public | ProductController | product-list.html, product-detail.html |
| 4 | Giỏ hàng | /cart, /cart/add, /cart/remove | User | CartController | cart.html |
| 5 | Thanh toán / Đặt hàng | /checkout | User | CartController, Order, OrderDetail, Bill | checkout.html |
| 6 | Lịch sử đơn hàng | /orders | User | UserOrderController | order-history.html |
| 7 | Quản lý người dùng | /admin/users | Admin | User, UserController | admin-user-list.html |
| 8 | Đánh giá sản phẩm | /review | User | Review, ReviewController, ReviewRepository | product-detail.html |
| 9 | Yêu thích (Wishlist) | /wishlist, /wishlist/toggle | User | Wishlist, WishlistController, WishlistRepository | wishlist.html, product-detail.html, product-list.html |
| 10 | Tồn kho (Inventory) | Tự động qua đặt hàng | User, Admin | Product, CartController | product-detail.html |
| 11 | Phân quyền người dùng | /admin/users/{id}/role | Admin | UserController | - |
| 12 | Khoá / Mở tài khoản | /admin/users/{id}/ban | Admin | UserController | - |
| 13 | Gửi email xác nhận đơn hàng | Tự động | User | MailService | - |
| 14 | Báo cáo doanh thu (Admin) | /admin/reports | Admin | ReportController, OrderRepository | admin-revenue-report.html |
| 15 | Xuất đơn hàng ra PDF | /admin/orders/export/pdf | Admin | OrderExportService | - |
| 16 | Xuất đơn hàng ra Excel | /admin/orders/export/excel | Admin | OrderExportService | - |
| 17 | Giao diện responsive | Tất cả | Tất cả | Bootstrap 5 | Toàn bộ giao diện |
| 18 | Giao diện đa ngôn ngữ (vi/en) | /lang/vi, /lang/en | Public | LocaleConfig, MessageSource | messages_vi.properties, messages_en.properties |
| 19 | Xác thực OTP qua email | /otp/verify | User | OtpService, OtpController | otp-verify.html |
| 20 | Bình luận sản phẩm | /comment | User | Comment, CommentController | product-detail.html |
| 21 | Thanh toán giả lập | /fake-payment | User | FakePaymentController | fake-payment.html |
| 22 | Wishlist toggle trên danh sách và chi tiết | /wishlist/toggle | User | WishlistController | product-list.html, product-detail.html |
