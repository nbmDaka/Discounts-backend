package com.discount_backend.Discount_backend.entity.discount;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@Table(
        name = "loved_discount",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "discount_id"})
)
public class LovedDiscount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    @Column(name = "discount_id", nullable = false, updatable = false)
    private Long discountId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    // constructors, getters…

    public LovedDiscount(Long userId, Long discountId) {
        this.userId = userId;
        this.discountId = discountId;
        this.createdAt = Instant.now();
    }

    // no-arg ctor is still needed by JPA
    protected LovedDiscount() {}
}
