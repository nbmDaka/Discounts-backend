package com.discount_backend.Discount_backend.controller;

import com.discount_backend.Discount_backend.dto.category.CategoryDto;
import com.discount_backend.Discount_backend.dto.category.CreateCategoryDto;
import com.discount_backend.Discount_backend.entity.objectfiles.ObjectType;
import com.discount_backend.Discount_backend.service.CategoryService;
import com.discount_backend.Discount_backend.service.ImageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService service;
    private final FileController fileController;

    public CategoryController(CategoryService categoryService, ImageService imageService) {
        this.service = categoryService;
        this.fileController = new FileController(imageService);
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
        return fileController.handleUpload(categoryId, ObjectType.CATEGORY, file);
    }

    @DeleteMapping("/{categoryId}/image")
    public ResponseEntity<Void> disableCategoryImage(
            @PathVariable Long categoryId) {
        fileController.handleDisableImage(categoryId, ObjectType.CATEGORY);
        return ResponseEntity.noContent().build();
    }
}