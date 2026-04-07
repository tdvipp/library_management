package com.library.management.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity                    // đánh dấu đây là entity
@Table(name = "borrow_records")     // tên bảng trong database
public class BorrowRecord {
    @Id                    // đây là primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // auto increment
    private Long id;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime dueDate;

    private LocalDateTime returnTime;

    private BigDecimal fine;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
}