// com/discount_backend/Discount_backend/repository/market/MarketRepository.java
package com.discount_backend.Discount_backend.repository.marketRepository;

import com.discount_backend.Discount_backend.entity.Market;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MarketRepository extends JpaRepository<Market, Long> {
    @Query("""
    SELECT m
      FROM Market m
      LEFT JOIN FETCH m.city
    ORDER BY m.name
  """)
    List<Market> findAllWithCity();

    @Query("""
    SELECT m
      FROM Market m
      LEFT JOIN FETCH m.city
    WHERE m.id = :id
  """)
    Optional<Market> findByIdWithCity(Long id);
}
