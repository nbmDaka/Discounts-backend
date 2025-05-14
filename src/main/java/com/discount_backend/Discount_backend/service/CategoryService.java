package com.discount_backend.Discount_backend.service;

import com.discount_backend.Discount_backend.dto.category.CategoryDto;
import com.discount_backend.Discount_backend.dto.category.CategoryMapper;
import com.discount_backend.Discount_backend.dto.category.CreateCategoryDto;
import com.discount_backend.Discount_backend.entity.Category;
import com.discount_backend.Discount_backend.exception.ResourceNotFoundException;
import com.discount_backend.Discount_backend.repository.categoryRepository.CategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryService {
    private final CategoryRepository repo;

    public CategoryService(CategoryRepository repo) {
        this.repo = repo;
    }

    public List<CategoryDto> getAll() {
        return repo.findAll().stream()
                .map(CategoryMapper::toDto)
                .collect(Collectors.toList());
    }

    public CategoryDto getById(Long id) {
        return repo.findById(id)
                .map(CategoryMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
    }

    public CategoryDto create(CreateCategoryDto dto) {
        Category saved = repo.save(CategoryMapper.toEntity(dto));
        return CategoryMapper.toDto(saved);
    }

    public CategoryDto update(Long id, CreateCategoryDto dto) {
        Category existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        return CategoryMapper.toDto(repo.save(existing));
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
