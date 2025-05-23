package com.discount_backend.Discount_backend.repository.role;


import com.discount_backend.Discount_backend.entity.role.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}