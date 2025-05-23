package com.discount_backend.Discount_backend.dto.market;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Setter
@Getter
public class MarketDto {
    private Long id;
    private String name;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String phone;
    private String websiteUrl;
    private Instant createdAt;
    private Instant updatedAt;
    private String  imageUrl;
    // getters & setters


}