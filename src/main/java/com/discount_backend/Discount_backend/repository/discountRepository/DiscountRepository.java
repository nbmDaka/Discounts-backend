package com.discount_backend.Discount_backend.repository.discountRepository;


import com.discount_backend.Discount_backend.entity.discount.Discount;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;

public interface DiscountRepository
        extends JpaRepository<Discount, Long>, JpaSpecificationExecutor<Discount> {
    @Modifying
    @Transactional
    int deleteByEndDateBefore(LocalDate date);
}
