# Money Tracker Web (Hệ thống quản lý tài chính cá nhân)

Money Tracker Web là một ứng dụng web giúp quản lý tài chính, ngân sách và theo dõi các khoản nợ cá nhân một cách hiệu quả, trực quan. Hệ thống được xây dựng trên nền tảng **Java Spring Boot**, sử dụng công cụ kết xuất giao diện **Thymeleaf** kết hợp với thư viện **Bootstrap 5** và **jQuery**.

---

## 🚀 Các tính năng chính

1. **Tổng quan tài chính (Dashboard)**:
   - Hiển thị biểu đồ thu chi, thống kê số dư tổng quan và tổng số nợ hiện có.

2. **Quản lý Tài khoản (Account Management)**:
   - Quản lý danh sách tài khoản tài chính (Thẻ ngân hàng, Ví điện tử, Tiền mặt...).
   - Theo dõi số dư thực tế theo thời gian thực.

3. **Quản lý Giao dịch (Transaction Management)**:
   - Ghi chép các khoản thu nhập và chi tiêu hàng ngày.
   - Phân loại giao dịch theo danh mục (Category) để dễ dàng kiểm soát cashflow.

4. **Quản lý Khoản nợ & Cho vay (Debt Management)**:
   - Phân loại rõ ràng giữa **Khoản đi vay (BORROW - Nợ phải trả)** và **Cho vay (LEND - Nợ cần thu)**.
   - Hỗ trợ thiết lập lãi suất linh hoạt:
     - Cách tính: **Lãi đơn (SIMPLE)** hoặc **Lãi kép (COMPOUND)**.
     - Kỳ hạn tính lãi: Theo **Tháng (MONTH)** hoặc theo **Năm (YEAR)**.
   - Biểu mẫu tạo nợ thông minh: Tự động đồng bộ hóa thời hạn vay (tháng) và ngày đáo hạn của khoản nợ một cách phản ứng.
   - Quản lý chi tiết từng khoản nợ:
     - **Lịch sử thanh toán thực tế**: Ghi chép chi tiết từng đợt trả nợ gốc và lãi kèm tài khoản nguồn/nhận trích tiền.
     - **Lịch trình thanh toán định kỳ dự kiến**: Tự động tính toán số tiền gốc và lãi dự kiến phải trả ở mỗi kỳ cho tới khi đáo hạn (Amortization Schedule).

5. **Quản lý Ngân sách & Tiết kiệm (Budget & Savings)**:
   - Thiết lập giới hạn chi tiêu theo danh mục.
   - Đặt ra các mục tiêu tiết kiệm tích lũy.

---

## 🛠️ Công nghệ sử dụng

- **Backend**:
  - Java 17
  - Spring Boot (Spring MVC, Spring Security với Cookie JWT, Spring Data JPA)
  - Hibernate / MySQL Database
  - Lombok
- **Frontend**:
  - Thymeleaf Template Engine
  - Bootstrap 5 (CSS / Layout)
  - jQuery & AJAX
  - FontAwesome (Icons)

---

## 📂 Cấu trúc dự án chính

```text
MoneyTrackerWeb/
├── src/main/java/money/
│   ├── controller/      # API Controllers và Web Controllers
│   ├── entity/          # JPA Entities (Debt, Transaction, Account, User...)
│   ├── repository/      # Spring Data JPA Repositories
│   ├── service/         # Interfaces và Implementations nghiệp vụ
│   └── dto/             # Data Transfer Objects
└── src/main/resources/
    ├── templates/       # Thymeleaf HTML Templates
    │   ├── layouts/     # Sidebar và Layout chung
    │   └── pages/       # Các trang chức năng (debt, repayment, account...)
    └── static/          # Assets tĩnh (CSS, Javascript, Images)
```

---

## ⚙️ Hướng dẫn cài đặt và chạy ứng dụng

### 1. Yêu cầu hệ thống
- Java Development Kit (JDK) 17 trở lên.
- Apache Maven (đã đi kèm Maven Wrapper).
- MySQL Server.

### 2. Cấu hình cơ sở dữ liệu
Chỉnh sửa tệp tin `src/main/resources/application.properties` để cấu hình kết nối MySQL của bạn:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/money_tracker?useSSL=false&serverTimezone=UTC
spring.datasource.username=YOUR_MYSQL_USERNAME
spring.datasource.password=YOUR_MYSQL_PASSWORD
spring.jpa.hibernate.ddl-auto=update
```

### 3. Khởi chạy ứng dụng
Chạy lệnh sau ở thư mục gốc của dự án:

- **Trên Windows (PowerShell)**:
  ```powershell
  .\mvnw spring-boot:run
  ```
- **Trên Linux/macOS**:
  ```bash
  ./mvnw spring-boot:run
  ```

Ứng dụng sẽ khởi chạy tại cổng mặc định `http://localhost:8080`. Bạn có thể đăng ký tài khoản mới và bắt đầu trải nghiệm!