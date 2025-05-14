package com.discount_backend.Discount_backend.repository.categoryRepository;


import com.discount_backend.Discount_backend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
