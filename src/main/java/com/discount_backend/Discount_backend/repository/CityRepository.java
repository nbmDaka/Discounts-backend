package com.discount_backend.Discount_backend.repository;

import com.discount_backend.Discount_backend.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Long> {
}