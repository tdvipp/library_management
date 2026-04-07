# Session 02 – Summary

## 1. Entity Layer hoàn chỉnh

### Book.java
- Không dùng `BookStatus` enum vì `availableCopies` đã đủ thông tin → tránh data redundancy
- Tên field dùng **camelCase** (`totalCopies`, `availableCopies`) – Hibernate tự map sang `snake_case` trong DB

### BorrowRecord.java
- `@ManyToOne` + `@JoinColumn(name = "user_id", nullable = false)` – khai báo FK
- Không khai báo thêm `private Long userId` vì `@ManyToOne` đã tự tạo cột FK
- `returnTime` và `fine` không có `nullable = false` vì chưa có giá trị khi mới tạo
- Dùng `LocalDateTime` (không dùng `java.sql.Date`), `BigDecimal` (không dùng `float/DecimalFormat`)

### @JoinColumn convention
```java
@ManyToOne
@JoinColumn(name = "user_id")  // tên cột FK trong bảng hiện tại
private User user;
// Nếu không ghi @JoinColumn → Hibernate tự đặt tên: {field}_{pk} = user_id
```

---

## 2. Repository Layer

```java
public interface BookRepository extends JpaRepository<Book, Long> {}
// JpaRepository<Entity, PKType> cho sẵn: findById, findAll, save, delete, existsById...
```

### JPA Specification – dynamic query
Dùng khi cần filter nhiều field, tránh tên method quá dài:

```java
// Thêm vào Repository
public interface BookRepository extends JpaRepository<Book, Long>,
                                         JpaSpecificationExecutor<Book> {}

// BookSpecification.java
public static Specification<Book> withFilter(BookFilterDTO filter) {
    return Specification
        .where(hasTitle(filter.getTitle()))
        .and(hasAuthor(filter.getAuthor()));
}

private static Specification<Book> hasTitle(String title) {
    return (root, query, cb) -> title == null ? null
        : cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
}
```
- `root` → entity (FROM book)
- `cb` → CriteriaBuilder, công cụ build điều kiện WHERE
- return `null` → bỏ qua điều kiện đó

**Tại sao dùng Specification thay vì `@Query`?**
→ Open/Closed Principle: thêm filter mới chỉ cần thêm method, không sửa code cũ.

---

## 3. Service Layer

### Interface + Implementation pattern
```java
// Interface – Controller phụ thuộc vào đây
public interface BookService { ... }

// Implementation – logic thực sự
@Service
public class BookServiceImpl implements BookService { ... }
```
→ Dependency Inversion: Controller không biết implementation cụ thể, dễ thay đổi sau.

### Constructor Injection (không dùng @Autowired)
```java
private final BookRepository bookRepository;  // final = không thể null

public BookServiceImpl(BookRepository bookRepository) {
    this.bookRepository = bookRepository;
}
```

### DRY – tách helper method tránh lặp code
```java
private BookResponseDTO toResponseDTO(Book book) { ... }

// Dùng method reference thay vì lambda
books.stream().map(this::toResponseDTO).toList();
```

### Business config – không hardcode
```properties
# application.properties
library.borrow.max-days=14
library.borrow.fine-per-day=5000
```
```java
@Value("${library.borrow.max-days:14}")  // :14 là giá trị mặc định
private int maxBorrowDays;
```

---

## 4. DTO Design

| DTO | Mục đích |
|-----|----------|
| `BookRequestDTO` | Nhận data từ frontend khi tạo/sửa |
| `BookFilterDTO` | Nhận query params để filter danh sách |
| `BookResponseDTO` | Trả data về frontend |

**Tại sao cần FilterDTO riêng?**
→ Filter là query params, không phải tạo mới resource. Gom vào 1 object để dễ mở rộng.

**RequestDTO không có `id`** vì `id` do server tự sinh, frontend không biết trước.

---

## 5. Controller Layer

```java
@RestController
@RequestMapping("/api/books")
public class BookController {

    @GetMapping           // GET /api/books
    @GetMapping("/{id}")  // GET /api/books/5
    @PostMapping          // POST /api/books
    @PatchMapping("/{id}")// PATCH /api/books/5
    @DeleteMapping("/{id}")// DELETE /api/books/5
}
```

### Annotations
| Annotation | Lấy data từ |
|-----------|-------------|
| `@PathVariable` | URL path: `/api/books/5` |
| `@RequestBody` | Request body (JSON) |
| (không có) | Query params: `?title=abc` |

### HTTP Status codes
```java
ResponseEntity.ok(...)                    // 200 OK
ResponseEntity.status(CREATED).body(...) // 201 Created
ResponseEntity.noContent().build()       // 204 No Content (DELETE)
```

---

## 6. Error Handling

### Custom Exception
```java
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
```

### Global Exception Handler
```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }
}
```
`@RestControllerAdvice` bắt exception từ tất cả controller → xử lý tập trung 1 chỗ.

---

## 7. JWT Authentication

### Flow đăng nhập
```
POST /api/auth/login {email, password}
→ tìm user theo email
→ bcrypt.matches(password, hash)
→ generateToken {email, role, exp} ký bằng SECRET_KEY
→ trả về token
```

### Flow gọi API có auth
```
Request + "Authorization: Bearer eyJ..."
→ JwtFilter verify token
→ extractEmail → load user
→ set Authentication vào SecurityContextHolder
→ SecurityConfig kiểm tra rule → cho đi tiếp hoặc 403
```

### JWT structure
```
header.payload.signature
```
- Server không lưu token – chỉ verify chữ ký
- Token hết hạn → user phải login lại

### BCrypt
```java
passwordEncoder.encode("123456")          // lưu vào DB
passwordEncoder.matches("123456", hash)   // verify khi login
```
BCrypt tự thêm salt ngẫu nhiên → cùng password hash ra khác nhau mỗi lần.

### SecurityConfig rules
```java
.requestMatchers("/api/auth/**").permitAll()          // login: không cần token
.requestMatchers(HttpMethod.GET, "/api/books/**").permitAll() // xem sách: không cần token
.anyRequest().authenticated()                          // còn lại: cần token
```

### SessionCreationPolicy.STATELESS
Server không lưu session – mỗi request tự mang token. Đặc trưng của REST API.

---

## 8. Circular Dependency

**Vấn đề:** `SecurityConfig` inject `JwtFilter`, `JwtFilter` cần `UserDetailsService` từ `SecurityConfig` → vòng tròn.

**Giải pháp:** Tách `UserDetailsService` và `PasswordEncoder` ra `AppConfig` riêng → không còn phụ thuộc vòng tròn.

---

## 9. Database

### Enum lưu dạng String
```java
@Enumerated(EnumType.STRING)  // lưu "LIBRARIAN" thay vì 1
@Column(nullable = false)
private Role role;
```
Tránh lỗi khi đổi thứ tự enum sau này.

### application.properties
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/library_management
spring.jpa.hibernate.ddl-auto=update  # tự tạo/cập nhật bảng
spring.jpa.show-sql=true              # log SQL ra console
```

### ddl-auto options
| Giá trị | Hành vi |
|---------|---------|
| `update` | Tạo/thêm column nếu chưa có, giữ data cũ |
| `create` | Xóa và tạo lại mỗi lần start |
| `none` | Không đụng schema (production) |
