package com.discount_backend.Discount_backend.dto.category;

import com.discount_backend.Discount_backend.entity.Category;

public class CategoryMapper {
    public static CategoryDto toDto(Category c) {
        CategoryDto dto = new CategoryDto();
        dto.setId(c.getId());
        dto.setName(c.getName());
        dto.setDescription(c.getDescription());
        return dto;
    }

    public static Category toEntity(CreateCategoryDto dto) {
        Category c = new Category();
        c.setName(dto.getName());
        c.setDescription(dto.getDescription());
        return c;
    }
}