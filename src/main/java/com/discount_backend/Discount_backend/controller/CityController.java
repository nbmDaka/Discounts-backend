package com.discount_backend.Discount_backend.controller;


import com.discount_backend.Discount_backend.dto.city.CityDto;
import com.discount_backend.Discount_backend.service.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cities")
@RequiredArgsConstructor
public class CityController {

    private final CityService cityService;

    @GetMapping
    public List<CityDto> getAllCities() {
        return cityService.getAllCities();
    }

    @PostMapping
    public CityDto createCity(@RequestBody CityDto dto) {
        return cityService.createCity(dto);
    }
}