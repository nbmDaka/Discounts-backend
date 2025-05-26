// com/discount_backend/Discount_backend/service/MarketService.java
package com.discount_backend.Discount_backend.service;

import com.discount_backend.Discount_backend.dto.market.*;
import com.discount_backend.Discount_backend.entity.City;
import com.discount_backend.Discount_backend.entity.Market;
import com.discount_backend.Discount_backend.entity.objectfiles.ObjectType;
import com.discount_backend.Discount_backend.exception.ResourceNotFoundException;
import com.discount_backend.Discount_backend.repository.CityRepository;
import com.discount_backend.Discount_backend.repository.marketRepository.MarketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.discount_backend.Discount_backend.dto.market.MarketMapper.toDto;
import static org.apache.tomcat.jni.SSLConf.apply;

@Service
@Transactional
public class MarketService {
    private final MarketRepository repo;
    private final ImageService imageService;
    private final CityRepository cityRepo;

    public MarketService(MarketRepository repo, ImageService imageService, CityRepository cityRepo) {
        this.repo = repo;
        this.imageService = imageService;
        this.cityRepo = cityRepo;
    }

    public List<MarketDto> getAll() {
        return repo.findAll().stream()
                .map(market -> {
                    MarketDto dto = toDto(market);
                    dto.setImageUrl(
                            imageService.getActiveImageUrl(ObjectType.MARKET, market.getId())
                    );
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public MarketDto getById(Long id) {
        Market m = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Market", id));

        MarketDto dto = toDto(m);
        dto.setImageUrl(
                imageService.getActiveImageUrl(ObjectType.MARKET, id)
        );
        return dto;
    }

    public MarketDto create(CreateMarketDto dto) {
        // 1. Map simple fields
        Market m = MarketMapper.toEntity(dto);

        // 2. Turn cityId into a real City and wire it onto m
        if (dto.getCityId() != null) {
            City c = cityRepo.findById(dto.getCityId())
                    .orElseThrow(() -> new IllegalArgumentException("City not found"));
            m.setCity(c);
        }

        // 3. Save, then map back to DTO (which will now include cityId/name)
        Market saved = repo.save(m);
        return MarketMapper.toDto(saved);
    }
    public MarketDto update(Long id, CreateMarketDto dto) {
        Market existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Market", id));


        if (dto.getName() != null) {
            existing.setName(dto.getName());
        }
        if (dto.getAddress() != null) {
            existing.setAddress(dto.getAddress());
        }
        if (dto.getLatitude() != null) {
            existing.setLatitude(dto.getLatitude());
        }
        if (dto.getLongitude() != null) {
            existing.setLongitude(dto.getLongitude());
        }
        if (dto.getPhone() != null) {
            existing.setPhone(dto.getPhone());
        }
        if (dto.getWebsiteUrl() != null) {
            existing.setWebsiteUrl(dto.getWebsiteUrl());
        }
        if (dto.getImageUrl() != null) {
            existing.setImageUrl(dto.getImageUrl());
        }

        if (dto.getCityId() != null) {
            City c = cityRepo.findById(dto.getCityId())
                    .orElseThrow(() -> new IllegalArgumentException("City not found"));
            existing.setCity(c);
        }


        Market updated = repo.save(existing);
        return toDto(updated);
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("Market", id);
        }
        repo.deleteById(id);
    }
}
