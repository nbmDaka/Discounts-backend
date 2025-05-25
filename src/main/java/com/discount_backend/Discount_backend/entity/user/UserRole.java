package com.discount_backend.Discount_backend.entity.user;


import com.discount_backend.Discount_backend.entity.role.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "user_roles")
public class UserRole {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Getter
    @ManyToOne(optional = false)
    @JoinColumn(name = "role_id")
    private Role role;

}
