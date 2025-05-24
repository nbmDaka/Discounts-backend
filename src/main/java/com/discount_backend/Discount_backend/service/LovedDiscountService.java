package com.discount_backend.Discount_backend.service;

import com.discount_backend.Discount_backend.entity.discount.LovedDiscount;
import com.discount_backend.Discount_backend.repository.discountRepository.DiscountRepository;
import com.discount_backend.Discount_backend.repository.discountRepository.LovedDiscountRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LovedDiscountService {
    private final LovedDiscountRepository lovedRepo;
    private final DiscountRepository discountRepo;

    public LovedDiscountService(LovedDiscountRepository lovedRepo,
                                DiscountRepository discountRepo) {
        this.lovedRepo = lovedRepo;
        this.discountRepo = discountRepo;
    }

    @Transactional
    public void addLove(Long userId, Long discountId) {
        // 1) ensure discount exists
        discountRepo.findById(discountId)
                .orElseThrow(() -> new EntityNotFoundException("Discount not found with id: " + discountId));
        // 2) only add if not already loved
        if (lovedRepo.findByUserIdAndDiscountId(userId, discountId).isEmpty()) {
            lovedRepo.save(new LovedDiscount(userId, discountId));
        }
        // otherwise no-op (idempotent)
    }

    public List<Long> getLovedDiscountIds(Long userId) {
        return lovedRepo.findByUserId(userId)
                .stream()
                .map(LovedDiscount::getDiscountId)
                .toList();
    }

    @Transactional
    public void removeLove(Long userId, Long discountId) {
        lovedRepo.deleteByUserIdAndDiscountId(userId, discountId);
        // deleteBy... is also idempotent
    }


}
