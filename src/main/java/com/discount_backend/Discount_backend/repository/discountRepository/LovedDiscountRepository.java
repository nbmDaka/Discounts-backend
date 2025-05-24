package com.discount_backend.Discount_backend.repository.discountRepository;

import com.discount_backend.Discount_backend.entity.discount.LovedDiscount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LovedDiscountRepository extends JpaRepository<LovedDiscount, Long> {
    Optional<LovedDiscount> findByUserIdAndDiscountId(Long userId, Long discountId);
    void deleteByUserIdAndDiscountId(Long userId, Long discountId);
    List<LovedDiscount> findByUserId(Long userId);
}
