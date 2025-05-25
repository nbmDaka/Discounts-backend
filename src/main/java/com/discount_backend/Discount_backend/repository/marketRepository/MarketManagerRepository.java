package com.discount_backend.Discount_backend.repository.marketRepository;

import com.discount_backend.Discount_backend.entity.MarketManagers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MarketManagerRepository extends JpaRepository<MarketManagers, Long> {
    Optional<MarketManagers> findByUserId(Long userId);
    void deleteByMarketIdAndUserId(Long marketId, Long userId);
}
