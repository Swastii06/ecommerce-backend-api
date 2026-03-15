package com.incture.ecommerceBackend.Repository;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.incture.ecommerceBackend.Entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

	// Find products by category and supports pages
	Page<Product> findByCategory(String category, Pageable pageable);

	// Find products under a certain price and supports pages
	Page<Product> findByPriceLessThanEqual(BigDecimal price, Pageable pageable);
}