package com.discount_backend.Discount_backend.dto.discount;

import com.discount_backend.Discount_backend.entity.Category;
import com.discount_backend.Discount_backend.entity.Market;
import com.discount_backend.Discount_backend.entity.discount.Discount;

public class DiscountMapper {
    public static DiscountDto toDto(Discount d) {
        DiscountDto dto = new DiscountDto();
        dto.setId(d.getId());
        dto.setTitle(d.getTitle());
        dto.setDescription(d.getDescription());
        dto.setMarketId(d.getMarket().getId());
        dto.setCategoryId(d.getCategory().getId());
        dto.setDiscountType(d.getDiscountType());
        dto.setDiscountValue(d.getDiscountValue());
        dto.setOriginalPrice(d.getOriginalPrice());
        dto.setDiscountedPrice(d.getDiscountedPrice());
        dto.setStartDate(d.getStartDate());
        dto.setEndDate(d.getEndDate());
        dto.setPremium(d.getPremium());
        return dto;
    }

    public static Discount toEntity(
            CreateDiscountDto dto,
            Market market,
            Category category
    ) {
        Discount d = new Discount();
        d.setTitle(dto.getTitle());
        d.setDescription(dto.getDescription());
        d.setMarket(market);
        d.setCategory(category);
        d.setDiscountType(dto.getDiscountType());
        d.setDiscountValue(dto.getDiscountValue());
        d.setOriginalPrice(dto.getOriginalPrice());
        d.setDiscountedPrice(dto.getDiscountedPrice());
        d.setStartDate(dto.getStartDate());
        d.setEndDate(dto.getEndDate());
        d.setPremium(dto.getIsPremium());
        return d;
    }
}