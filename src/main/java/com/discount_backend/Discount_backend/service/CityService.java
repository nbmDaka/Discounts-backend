package com.discount_backend.Discount_backend.service;


import com.discount_backend.Discount_backend.dto.city.CityDto;
import com.discount_backend.Discount_backend.entity.City;
import com.discount_backend.Discount_backend.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CityService {

    private final CityRepository cityRepository;

    public List<CityDto> getAllCities() {
        return cityRepository.findAll()
                .stream()
                .map(city -> {
                    CityDto dto = new CityDto();
                    dto.setId(city.getId());
                    dto.setName(city.getName());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public CityDto createCity(CityDto dto) {
        City city = new City();
        city.setName(dto.getName());
        City saved = cityRepository.save(city);
        dto.setId(saved.getId());
        return dto;
    }
}