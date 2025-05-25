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
import jakarta.persistence.criteria.Expression;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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
            Optional<Boolean> isPremium,
            Optional<Long> marketId,
            Optional<Integer> minPercent,
            Optional<String> startDate
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

        if (marketId.isPresent()) {
            spec = spec.and((root, q, cb) ->
                    cb.equal(root.get("market").get("id"), marketId.get()));
        }

        if (minPercent.isPresent()) {
            Integer percent = minPercent.get();
            spec = spec.and((root, q, cb) -> {
                Expression<Number> original = root.get("originalPrice");
                Expression<Number> discounted = root.get("discountedPrice");

                // Calculate (original - discounted) / original * 100
                Expression<Number> discountRaw = cb.prod(
                        cb.quot(cb.diff(original, discounted), original),
                        cb.literal(100)
                );

                // Cast to Double for comparison
                Expression<Double> discountPercent = discountRaw.as(Double.class);

                return cb.greaterThanOrEqualTo(discountPercent, percent.doubleValue());
            });
        }



        if (startDate.isPresent()) {
            try {
                LocalDate date = LocalDate.parse(startDate.get());
                spec = spec.and((root, q, cb) ->
                        cb.greaterThanOrEqualTo(root.get("startDate"), date));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid date format. Use yyyy-MM-dd");
            }
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
