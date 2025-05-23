// com/discount_backend/Discount_backend/service/MarketService.java
package com.discount_backend.Discount_backend.service;

import com.discount_backend.Discount_backend.dto.market.*;
import com.discount_backend.Discount_backend.entity.Market;
import com.discount_backend.Discount_backend.entity.objectfiles.ObjectType;
import com.discount_backend.Discount_backend.exception.ResourceNotFoundException;
import com.discount_backend.Discount_backend.repository.marketRepository.MarketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.apache.tomcat.jni.SSLConf.apply;

@Service
@Transactional
public class MarketService {
    private final MarketRepository repo;
    private final ImageService imageService;

    public MarketService(MarketRepository repo, ImageService imageService) {
        this.repo = repo;
        this.imageService = imageService;
    }

    public List<MarketDto> getAll() {
        return repo.findAll().stream()
                .map(market -> {
                    MarketDto dto = MarketMapper.toDto(market);
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

        MarketDto dto = MarketMapper.toDto(m);
        dto.setImageUrl(
                imageService.getActiveImageUrl(ObjectType.MARKET, id)
        );
        return dto;
    }

    public MarketDto create(CreateMarketDto dto) {
        Market saved = repo.save(MarketMapper.toEntity(dto));
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


        Market updated = repo.save(existing);
        return MarketMapper.toDto(updated);
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("Market", id);
        }
        repo.deleteById(id);
    }
}
