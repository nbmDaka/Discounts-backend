package com.discount_backend.Discount_backend.repository.user;


import com.discount_backend.Discount_backend.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}