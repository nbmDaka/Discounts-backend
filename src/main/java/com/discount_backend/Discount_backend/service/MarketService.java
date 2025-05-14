// com/discount_backend/Discount_backend/service/MarketService.java
package com.discount_backend.Discount_backend.service;

import com.discount_backend.Discount_backend.dto.market.*;
import com.discount_backend.Discount_backend.entity.Market;
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

    public MarketService(MarketRepository repo) {
        this.repo = repo;
    }

    public List<MarketDto> getAll() {
        return repo.findAll().stream()
                .map(MarketMapper::toDto)
                .collect(Collectors.toList());
    }

    public MarketDto getById(Long id) {
        return repo.findById(id)
                .map(MarketMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Market", id));
    }

    public MarketDto create(CreateMarketDto dto) {
        Market saved = repo.save(MarketMapper.toEntity(dto));
        return MarketMapper.toDto(saved);
    }
    public MarketDto update(Long id, CreateMarketDto dto) {
        Market existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Market", id));

        existing.setName(dto.getName());
        existing.setAddress(dto.getAddress());
        existing.setLatitude(dto.getLatitude());
        existing.setLongitude(dto.getLongitude());
        existing.setPhone(dto.getPhone());
        existing.setWebsiteUrl(dto.getWebsiteUrl());
        existing.setLogoUrl(dto.getLogoUrl());

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
