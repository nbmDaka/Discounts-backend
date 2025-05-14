package com.discount_backend.Discount_backend.repository.auth;


import com.discount_backend.Discount_backend.entity.auth.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);
}