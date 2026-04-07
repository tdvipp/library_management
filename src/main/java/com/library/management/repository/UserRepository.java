package com.library.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.library.management.entity.*;

public interface UserRepository extends JpaRepository<User, Long> {
    // Không cần viết gì thêm!
    // Đã có sẵn: findById, findAll, save, delete, ...

    java.util.Optional<User> findByEmail(String email);
}
