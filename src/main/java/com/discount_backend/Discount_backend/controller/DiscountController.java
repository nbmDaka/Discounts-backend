package com.discount_backend.Discount_backend.controller;

import com.discount_backend.Discount_backend.dto.discount.CreateDiscountDto;
import com.discount_backend.Discount_backend.dto.discount.DiscountDto;
import com.discount_backend.Discount_backend.entity.objectfiles.ObjectType;
import com.discount_backend.Discount_backend.service.DiscountService;
import com.discount_backend.Discount_backend.service.ImageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/discounts")
public class DiscountController {
    private final DiscountService service;
    private final FileController fileController;

    public DiscountController(DiscountService service, ImageService imageService) {
        this.service = service;
        this.fileController = new FileController(imageService);
    }

    @GetMapping
    public List<DiscountDto> all(
            @RequestParam Optional<Long> category,
            @RequestParam Optional<Boolean> premium,
            @RequestParam Optional<Long> market,
            @RequestParam Optional<Integer> minPercent,
            @RequestParam Optional<String> startDate // use ISO format: yyyy-MM-dd
    ) {
        return service.getAll(category, premium, market, minPercent, startDate);
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

    @PostMapping("/{discountId}/image")
    public ResponseEntity<String> uploadDiscountImage(@PathVariable Long discountId,
                                                      @RequestParam MultipartFile file) {
        return fileController.handleUpload(discountId, ObjectType.DISCOUNT, file);
    }

    @DeleteMapping("/{discountId}/image")
    public ResponseEntity<Void> disableDiscountImage(
            @PathVariable Long discountId) {
        fileController.handleDisableImage(discountId, ObjectType.DISCOUNT);
        return ResponseEntity.noContent().build();
    }
}