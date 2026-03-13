package com.incture.ecommerceBackend.Service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.incture.ecommerceBackend.Entity.Product;
import com.incture.ecommerceBackend.Exception.CustomException;
import com.incture.ecommerceBackend.Repository.ProductRepository;

@Service
public class ProductService {

	private final ProductRepository productRepository;

	@Autowired
	public ProductService(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	// 1. Add a new product
	public Product addProduct(Product product) {
		return productRepository.save(product);
	}

	// 2. View products with Pagination and Filtering (Added category and maxPrice)
	public Page<Product> getAllProducts(int page, int size, String sortBy, String category, BigDecimal maxPrice) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

		// If user wants to filter by category
		if (category != null && !category.isEmpty()) {
			return productRepository.findByCategory(category, pageable);
		}
		// If user wants to filter by a maximum price
		else if (maxPrice != null) {
			return productRepository.findByPriceLessThanEqual(maxPrice, pageable);
		}

		// If no filters are provided, return everything
		return productRepository.findAll(pageable);
	}

	// 3. Get a single product by ID (Updated to CustomException)
	public Product getProductById(Long id) {
		return productRepository.findById(id)
				.orElseThrow(() -> new CustomException("Product not found with ID: " + id));
	}

	// 4. Update an existing product
	public Product updateProduct(Long id, Product updatedProduct) {
		Product existingProduct = getProductById(id);

		existingProduct.setName(updatedProduct.getName());
		existingProduct.setDescription(updatedProduct.getDescription());
		existingProduct.setPrice(updatedProduct.getPrice());
		existingProduct.setStock(updatedProduct.getStock());
		existingProduct.setCategory(updatedProduct.getCategory());
		existingProduct.setImageUrl(updatedProduct.getImageUrl());
		existingProduct.setRating(updatedProduct.getRating());

		return productRepository.save(existingProduct);
	}

	// 5. Delete a product
	public void deleteProduct(Long id) {
		productRepository.deleteById(id);
	}
}