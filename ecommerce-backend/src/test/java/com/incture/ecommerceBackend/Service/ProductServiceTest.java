package com.incture.ecommerceBackend.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.incture.ecommerceBackend.Entity.Product;
import com.incture.ecommerceBackend.Repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

	@Mock
	private ProductRepository productRepository; // The "fake" db that simulates db behaivior without using real db

	@InjectMocks
	private ProductService productService; // The real service we are testing is injected with mock ProductRepository
											// into it

	private Product testProduct;
	// A sample product object used for testing.

	@BeforeEach // This method runs before every test case & is used to initialize test data.

	void setUp() {
		System.out.println("Executing @BeforeEach: Setting up fake product data...");
		testProduct = new Product();
		testProduct.setId(1L);
		testProduct.setName("Gaming Laptop");
		testProduct.setPrice(new BigDecimal("1200.00"));
		testProduct.setStock(10);
	}

	@DisplayName("Test Adding a Product") // Provides a readable name for the test case

	@Test // Marks this method as a test method to be executed by JUnit
	void testAddProduct() {
		System.out.println("Running Test: Add Product");

		// Arrange: Tell the fake database what to do when someone tries to save a
		// product
		when(productRepository.save(any(Product.class))).thenReturn(testProduct);

		// Act: Actually call the method in our service
		Product savedProduct = productService.addProduct(testProduct);

		// Assert: Verify the results
		assertNotNull(savedProduct);
		assertEquals("Gaming Laptop", savedProduct.getName());
		assertEquals(new BigDecimal("1200.00"), savedProduct.getPrice());
	}
}