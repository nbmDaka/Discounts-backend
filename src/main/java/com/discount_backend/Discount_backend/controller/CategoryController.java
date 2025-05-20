package com.discount_backend.Discount_backend.controller;

import com.discount_backend.Discount_backend.dto.category.CategoryDto;
import com.discount_backend.Discount_backend.dto.category.CreateCategoryDto;
import com.discount_backend.Discount_backend.entity.objectfiles.ObjectType;
import com.discount_backend.Discount_backend.service.CategoryService;
import com.discount_backend.Discount_backend.service.ImageService;
import com.discount_backend.Discount_backend.controller.BaseUploadController;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService service;
    private final BaseUploadController uploadController;

    public CategoryController(CategoryService categoryService, ImageService imageService) {
        this.service = categoryService;
        this.uploadController = new BaseUploadController(imageService);
    }

    @GetMapping
    public List<CategoryDto> all() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public CategoryDto one(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto create(@Valid @RequestBody CreateCategoryDto dto) {
        return service.create(dto);
    }

    @PatchMapping("/{id}")
    public CategoryDto update(
            @PathVariable Long id,
            @RequestBody CreateCategoryDto dto
    ) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PostMapping("/{categoryId}/image")
    public ResponseEntity<String> uploadCategoryImage(
            @PathVariable Long categoryId,
            @RequestParam MultipartFile file) {
            if (file == null || file.isEmpty()) {
                return ResponseEntity
                        .badRequest()
                        .body("File must not be empty");
            }
        return uploadController.handleUpload(categoryId, ObjectType.CATEGORY, file);
    }
}