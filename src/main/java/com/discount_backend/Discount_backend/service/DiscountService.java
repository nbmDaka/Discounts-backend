package com.discount_backend.Discount_backend.service;

import com.discount_backend.Discount_backend.dto.discount.CreateDiscountDto;
import com.discount_backend.Discount_backend.dto.discount.DiscountDto;
import com.discount_backend.Discount_backend.dto.discount.DiscountMapper;
import com.discount_backend.Discount_backend.entity.*;
import com.discount_backend.Discount_backend.entity.category.Category;
import com.discount_backend.Discount_backend.entity.discount.Discount;
import com.discount_backend.Discount_backend.entity.objectfiles.ObjectType;
import com.discount_backend.Discount_backend.exception.ResourceNotFoundException;
import com.discount_backend.Discount_backend.repository.categoryRepository.CategoryRepository;
import com.discount_backend.Discount_backend.repository.discountRepository.DiscountRepository;
import com.discount_backend.Discount_backend.repository.marketRepository.MarketRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class DiscountService {
    private final DiscountRepository repo;
    private final MarketRepository marketRepo;
    private final CategoryRepository categoryRepo;
    private final ImageService imageService;

    public DiscountService(
            DiscountRepository repo,
            MarketRepository marketRepo,
            CategoryRepository categoryRepo,
            ImageService imageService
    ) {
        this.repo = repo;
        this.marketRepo = marketRepo;
        this.categoryRepo = categoryRepo;
        this.imageService = imageService;
    }

    public List<DiscountDto> getAll(
            Optional<Long> categoryId,
            Optional<Boolean> isPremium
    ) {
        Specification<Discount> spec = Specification.where(null);

        if (categoryId.isPresent()) {
            spec = spec.and((root, q, cb) ->
                    cb.equal(root.get("category").get("id"), categoryId.get()));
        }
        if (isPremium.isPresent()) {
            spec = spec.and((root, q, cb) ->
                    cb.equal(root.get("isPremium"), isPremium.get()));
        }

        return repo.findAll(spec).stream()
                .map(discount -> {
                    DiscountDto dto = DiscountMapper.toDto(discount);
                    dto.setImageUrl(
                            imageService.getActiveImageUrl(ObjectType.DISCOUNT, discount.getId())
                    );
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public DiscountDto getById(Long id) {
        Discount d = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Discount", id));

        DiscountDto dto = DiscountMapper.toDto(d);
        dto.setImageUrl(
                imageService.getActiveImageUrl(ObjectType.DISCOUNT, id)
        );
        return dto;
    }
    public DiscountDto create(CreateDiscountDto dto) {
        Market market = marketRepo.findById(dto.getMarketId())
                .orElseThrow(() -> new ResourceNotFoundException("Market", dto.getMarketId()));
        Category category = categoryRepo.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", dto.getCategoryId()));
        Discount saved = repo.save(DiscountMapper.toEntity(dto, market, category));
        return DiscountMapper.toDto(saved);
    }

    public DiscountDto update(Long id, CreateDiscountDto dto) {
        Discount existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Discount", id));
        Market market = marketRepo.findById(dto.getMarketId())
                .orElseThrow(() -> new ResourceNotFoundException("Market", dto.getMarketId()));
        Category category = categoryRepo.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", dto.getCategoryId()));

        existing.setTitle(dto.getTitle());
        existing.setDescription(dto.getDescription());
        existing.setMarket(market);
        existing.setCategory(category);
        existing.setDiscountType(dto.getDiscountType());
        existing.setDiscountValue(dto.getDiscountValue());
        existing.setOriginalPrice(dto.getOriginalPrice());
        existing.setDiscountedPrice(dto.getDiscountedPrice());
        existing.setStartDate(dto.getStartDate());
        existing.setEndDate(dto.getEndDate());
        existing.setPremium(dto.getIsPremium());

        return DiscountMapper.toDto(repo.save(existing));
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
