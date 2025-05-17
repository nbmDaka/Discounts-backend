package com.discount_backend.Discount_backend.entity.discount;

import com.discount_backend.Discount_backend.entity.category.Category;
import com.discount_backend.Discount_backend.entity.Market;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "discounts")
public class Discount {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "market_id", nullable = false)
    private Market market;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private DiscountType discountType;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal discountValue;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal originalPrice;

    @Column(precision = 12, scale = 2)
    private BigDecimal discountedPrice;

    private LocalDate startDate;
    private LocalDate endDate;

    @Column(name = "is_premium", nullable = false)
    private Boolean isPremium = false;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    @PreUpdate
    public void preUpdate() { updatedAt = Instant.now(); }

    // getters & setters

    public void setPremium(Boolean premium) {
        isPremium = premium;
    }

}

