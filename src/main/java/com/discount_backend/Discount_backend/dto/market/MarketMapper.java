// com/discount_backend/Discount_backend/dto/market/MarketMapper.java
package com.discount_backend.Discount_backend.dto.market;

import com.discount_backend.Discount_backend.entity.Market;

public class MarketMapper {
    public static MarketDto toDto(Market m) {
        MarketDto dto = new MarketDto();
        dto.setId(m.getId());
        dto.setName(m.getName());
        dto.setAddress(m.getAddress());
        dto.setLatitude(m.getLatitude());
        dto.setLongitude(m.getLongitude());
        dto.setPhone(m.getPhone());
        dto.setWebsiteUrl(m.getWebsiteUrl());
        dto.setImageUrl(m.getImageUrl());
        dto.setCreatedAt(m.getCreatedAt());
        dto.setUpdatedAt(m.getUpdatedAt());
        return dto;
    }

    public static Market toEntity(CreateMarketDto dto) {
        Market m = new Market();
        m.setName(dto.getName());
        m.setAddress(dto.getAddress());
        m.setLatitude(dto.getLatitude());
        m.setLongitude(dto.getLongitude());
        m.setPhone(dto.getPhone());
        m.setWebsiteUrl(dto.getWebsiteUrl());
        m.setImageUrl(dto.getImageUrl());
        return m;
    }
}
