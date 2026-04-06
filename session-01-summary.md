# Session 01 – Tổng Kết Kiến Thức

## 1. Quy Trình Trước Khi Code

```
1. Clarify Requirements   →  làm rõ yêu cầu mờ / thiếu
2. Define Entities        →  xác định các thực thể trong hệ thống
3. Database Design        →  thiết kế schema / ERD
4. API Design             →  định nghĩa các endpoint
5. Architecture Design    →  cấu trúc code (layers, packages)
6. Code                   →  mới bắt đầu gõ
```

---

## 2. Database Design

### Entities
| Entity | Vai trò |
|---|---|
| `User` | Sinh viên & Thủ thư (gộp chung, phân biệt bằng `role`) |
| `Book` | Thông tin sách |
| `BorrowRecord` | Lịch sử mượn sách |

### Quan hệ
```
User         1 ──── n    BorrowRecord
Book         1 ──── n    BorrowRecord
```

### Schema

**User**
```
id, name, email, password, role (STUDENT/LIBRARIAN), code
```

**Book**
```
id, title, description, author, category, total_copies, available_copies
```

**BorrowRecord**
```
id, user_id, book_id, start_time, due_date, return_time (nullable), fine (decimal)
```

### Trade-off Thiết Kế Database

| | 2 bảng riêng | 1 bảng chung | 3 bảng (chung + mở rộng) |
|---|---|---|---|
| Đơn giản | ✅ | ✅✅ | ❌ |
| Dễ mở rộng | ✅ (độc lập) | ❌ (bảng phình to, nhiều NULL) | ✅✅ |
| Chọn khi | Khác biệt lớn | Rất giống nhau, ít mở rộng | Giống nhau + cần mở rộng |

> **Bài học:** Mỗi quyết định thiết kế đều có đánh đổi. Quan trọng là biết mình đang đánh đổi cái gì.

### Lưu Ý Quan Trọng
- **Data redundancy**: Không lưu dữ liệu có thể tính được. Ví dụ: `borrowed_copies = total - available` → không cần lưu `borrowed_copies`
- **decimal vs float**: Tiền luôn dùng `decimal` – `float` có sai số nhỏ, tích lũy nhiều lần sẽ gây lỗi

---

## 3. API Design (REST)

### HTTP Methods
| Method | Ý nghĩa | Khi nào dùng |
|---|---|---|
| `GET` | Lấy dữ liệu | Đọc, không thay đổi gì trên server |
| `POST` | Tạo mới / gửi dữ liệu | Tạo resource mới, hoặc action không phải CRUD |
| `PUT` | Thay thế toàn bộ | Update toàn bộ resource |
| `PATCH` | Cập nhật một phần | Update một số field của resource |
| `DELETE` | Xóa | Xóa resource |

### REST Conventions
```
# Dùng danh từ số nhiều
✅ /api/books
❌ /api/book, /api/getBooks

# Dùng kebab-case
✅ /api/borrow-records
❌ /api/borrowRecords, /api/borrow_records

# Query params cho filter/search
GET /api/books?title=Clean+Code

# Action đặc biệt → thêm verb vào cuối
PATCH /api/borrow-records/{id}/return
POST  /api/auth/login
```

### Endpoints

```
# Books
GET    /api/books              → danh sách (filter: ?title=...)
GET    /api/books/{id}         → chi tiết 1 cuốn
POST   /api/books              → thêm sách mới (body: data)
PATCH  /api/books/{id}         → cập nhật sách
DELETE /api/books/{id}         → xóa sách

# Borrow Records
GET    /api/borrow-records     → danh sách (filter: ?user_id=...)
POST   /api/borrow-records     → tạo lượt mượn mới (body: user_id, book_id)
PATCH  /api/borrow-records/{id}/return  → trả sách

# Users
GET    /api/users              → danh sách users
GET    /api/users/{id}         → thông tin 1 user

# Auth
POST   /api/auth/login
POST   /api/auth/logout
```

### Tại sao đăng nhập dùng POST?
- `GET` không có request body → password lộ trên URL/logs
- `POST` gửi data qua body, mã hóa qua HTTPS
- Đăng nhập là **action**, không phải tạo resource

---

## 4. Layered Architecture

```
Request
   ↓
┌─────────────────────┐
│   Controller Layer  │  ← Nhận request, trả response (JSON)
├─────────────────────┤
│   Service Layer     │  ← Xử lý business logic
├─────────────────────┤
│  Repository Layer   │  ← Giao tiếp với database
├─────────────────────┤
│   Entity/Model      │  ← Đại diện cho dữ liệu
└─────────────────────┘
   ↓
Database
```

**Quy tắc:** Mỗi layer chỉ giao tiếp với layer liền kề. Controller không gọi thẳng Repository.

**Tại sao cần Service layer?**
- Business logic tập trung 1 chỗ → sửa 1 chỗ, không sót
- Controller chỉ lo nhận/trả request → Single Responsibility

**MVC vs Layered Architecture:**
- MVC → app có giao diện server-side (render HTML)
- Layered Architecture → REST API (trả JSON, frontend tự lo hiển thị)

---

## 5. DTO – Data Transfer Object

**DTO là object dùng để truyền dữ liệu giữa các tầng**, đặc biệt giữa Controller và client (frontend).

Có 2 loại:
- `RequestDTO` → nhận data từ frontend (input)
- `ResponseDTO` → trả data về cho frontend (output)

**Tại sao không dùng Entity trực tiếp?**
- Entity có thể chứa field nhạy cảm (password, internal id...)
- Entity map 1-1 với database, không phải lúc nào cũng phù hợp với frontend
- Tách biệt database schema và API contract → thay đổi DB không ảnh hưởng API

Ví dụ:
```java
// Entity – map với database
class Book {
    Long id;
    String title;
    String author;
    int totalCopies;       // frontend không cần biết
    int availableCopies;
}

// RequestDTO – nhận từ frontend khi tạo sách mới
class BookRequestDTO {
    String title;
    String author;
    String category;
    int totalCopies;
}

// ResponseDTO – trả về cho frontend
class BookResponseDTO {
    Long id;
    String title;
    String author;
    int availableCopies;   // chỉ trả những gì cần thiết
}
```

---

## 6. Cấu Trúc Package

```
src/main/java/com/library/management/
├── controller/
│   ├── AuthController.java
│   ├── BookController.java
│   ├── BorrowRecordController.java
│   └── UserController.java
├── service/
│   ├── BookService.java              ← interface
│   ├── BorrowRecordService.java
│   ├── UserService.java
│   └── impl/
│       ├── BookServiceImpl.java      ← implementation
│       ├── BorrowRecordServiceImpl.java
│       └── UserServiceImpl.java
├── repository/
│   ├── BookRepository.java
│   ├── BorrowRecordRepository.java
│   └── UserRepository.java
├── entity/
│   ├── Book.java
│   ├── BorrowRecord.java
│   ├── Role.java                     ← enum
│   └── User.java
└── dto/
    ├── request/
    │   ├── BookRequestDTO.java
    │   ├── BorrowRecordRequestDTO.java
    │   └── LoginRequestDTO.java
    └── response/
        ├── BookResponseDTO.java
        ├── BorrowRecordResponseDTO.java
        └── UserResponseDTO.java
```

**Tại sao Service tách interface và impl?**
→ Dependency Inversion (chữ D trong SOLID): Controller phụ thuộc vào `BookService` (interface), không phụ thuộc vào `BookServiceImpl` (implementation) → dễ thay đổi implementation, dễ test.

---

## 6. Java / JPA Cơ Bản

### JPA Annotations
```java
@Entity                    // class này map với 1 bảng DB
@Table(name = "users")     // tên bảng
public class User {

    @Id                    // primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // auto increment
    private Long id;

    @Column(nullable = false)           // NOT NULL
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @ManyToOne                         // quan hệ n-1
    @JoinColumn(name = "user_id")
    private User user;
}
```

### Lombok
```java
@Data   // tự generate getter, setter, toString, equals, hashCode
public class User { ... }
```

### Encapsulation
- Class → `public`
- Fields → `private` (truy cập qua getter/setter)
- Method public → API ra bên ngoài
- Method private → logic nội bộ

### Enum
```java
public enum Role {
    STUDENT, LIBRARIAN
}

public enum BookStatus {
    AVAILABLE, BORROWED, UNAVAILABLE
}
```

---

## 7. Nguyên Tắc SOLID (Đã Gặp)

| Nguyên tắc | Ví dụ trong project |
|---|---|
| **S** – Single Responsibility | Controller chỉ lo request/response, Service lo business logic |
| **D** – Dependency Inversion | Controller dùng `BookService` interface, không dùng `BookServiceImpl` |
| **O** – Open/Closed | Thêm implementation mới mà không sửa Controller |

---

## 8. Các Nguyên Tắc Khác

- **YAGNI** – You Aren't Gonna Need It: không build thứ chưa chắc cần
- **DRY** – Don't Repeat Yourself: business logic tập trung 1 chỗ
- **Data redundancy**: không lưu dữ liệu tính được
- **Blast radius**: tránh API có thể gây thiệt hại lớn (vd: DELETE không có id)

---

## 9. Bài Tập Về Nhà

- [ ] Viết `Book.java` với enum `BookStatus`
- [ ] Viết `BorrowRecord.java` với `@ManyToOne` cho `User` và `Book`
