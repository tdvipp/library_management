package com.library.management.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity                    // đánh dấu đây là entity
@Table(name = "books")     // tên bảng trong database
public class Book {
    @Id                    // đây là primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // auto increment
    private Long id;

    @Column(nullable = false)   // NOT NULL
    private String title;

    @Column
    private String description;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private int totalCopies;

    @Column(nullable = false)
    private int availableCopies;
}