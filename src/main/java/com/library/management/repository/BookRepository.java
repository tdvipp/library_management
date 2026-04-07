package com.library.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.library.management.entity.*;

public interface BookRepository extends JpaRepository<Book, Long>, 
                                         JpaSpecificationExecutor<Book> {

    // Không cần viết gì thêm!
    // Đã có sẵn: findById, findAll, save, delete, ...
    List<Book> findByTitleContainingIgnoreCase(String title);
}
