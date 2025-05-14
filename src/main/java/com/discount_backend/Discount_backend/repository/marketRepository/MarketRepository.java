// com/discount_backend/Discount_backend/repository/market/MarketRepository.java
package com.discount_backend.Discount_backend.repository.marketRepository;

import com.discount_backend.Discount_backend.entity.Market;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketRepository extends JpaRepository<Market, Long> {
}
