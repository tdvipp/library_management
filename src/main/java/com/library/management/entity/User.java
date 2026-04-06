package com.library.management.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity                    // đánh dấu đây là entity
@Table(name = "users")     // tên bảng trong database
public class User {

    @Id                    // đây là primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // auto increment
    private Long id;

    @Column(nullable = false)   // NOT NULL
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Role role;

    @Column(unique = true, nullable = false)
    private int code;
}

