package com.discount_backend.Discount_backend.dto.category;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CategoryDto {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;

}
