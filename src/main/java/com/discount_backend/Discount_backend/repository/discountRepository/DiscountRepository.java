package com.discount_backend.Discount_backend.repository.discountRepository;


import com.discount_backend.Discount_backend.entity.discount.Discount;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DiscountRepository
        extends JpaRepository<Discount, Long>, JpaSpecificationExecutor<Discount> {}
