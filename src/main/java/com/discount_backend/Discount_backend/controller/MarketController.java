// com/discount_backend/Discount_backend/controller/MarketController.java
package com.discount_backend.Discount_backend.controller;

import com.discount_backend.Discount_backend.dto.market.*;
import com.discount_backend.Discount_backend.service.MarketService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/markets")
public class MarketController {
    private final MarketService service;

    public MarketController(MarketService service) {
        this.service = service;
    }

    @GetMapping
    public List<MarketDto> all() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public MarketDto one(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MarketDto create(@Valid @RequestBody CreateMarketDto dto) {
        return service.create(dto);
    }

    @PatchMapping("/{id}")
    public MarketDto update(
            @PathVariable Long id,
            @RequestBody CreateMarketDto dto
    ) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
