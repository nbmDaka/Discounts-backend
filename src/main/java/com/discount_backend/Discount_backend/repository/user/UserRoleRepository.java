package com.discount_backend.Discount_backend.repository.user;

import com.discount_backend.Discount_backend.entity.role.Role;
import com.discount_backend.Discount_backend.entity.user.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    boolean existsByUserIdAndRole(Long userId, Role role);

    @Modifying
    @Query("DELETE FROM UserRole ur WHERE ur.user.id = :userId AND ur.role.id = :roleId")
    void deleteByUserIdAndRoleId(Long userId, Long roleId);
}