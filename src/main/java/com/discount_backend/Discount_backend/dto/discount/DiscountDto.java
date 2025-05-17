package com.discount_backend.Discount_backend.dto.discount;

import com.discount_backend.Discount_backend.entity.discount.DiscountType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class DiscountDto {
    private Long id;
    private String title;
    private String description;
    private Long marketId;
    private Long categoryId;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal originalPrice;
    private BigDecimal discountedPrice;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isPremium;
    // getters & setters

}