package com.discount_backend.Discount_backend.repository;

import com.discount_backend.Discount_backend.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    // findByUserId, etc. can be added if needed
}