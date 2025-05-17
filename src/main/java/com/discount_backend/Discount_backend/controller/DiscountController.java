package com.discount_backend.Discount_backend.controller;

import com.discount_backend.Discount_backend.dto.discount.CreateDiscountDto;
import com.discount_backend.Discount_backend.dto.discount.DiscountDto;
import com.discount_backend.Discount_backend.service.DiscountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/discounts")
public class DiscountController {
    private final DiscountService service;

    public DiscountController(DiscountService service) {
        this.service = service;
    }

    @GetMapping
    public List<DiscountDto> all(
            @RequestParam Optional<Long> category,
            @RequestParam Optional<Boolean> premium
    ) {
        return service.getAll(category, premium);
    }

    @GetMapping("/{id}")
    public DiscountDto one(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DiscountDto create(@Valid @RequestBody CreateDiscountDto dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public DiscountDto update(
            @PathVariable Long id,
            @Valid @RequestBody CreateDiscountDto dto
    ) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}