package com.discount_backend.Discount_backend.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateCategoryDto {
    @NotBlank
    @Size(max = 100)
    private String name;

    private String description;

    private String imageUrl;
    // getters & setters

}