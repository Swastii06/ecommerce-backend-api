package com.incture.ecommerceBackend.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.incture.ecommerceBackend.Entity.Product;
import com.incture.ecommerceBackend.Exception.CustomException;
import com.incture.ecommerceBackend.Repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

	@Mock
	private ProductRepository productRepository; // The "fake" db that simulates db behavior

	@InjectMocks
	private ProductService productService; // The real service injected with the mock repo

	private Product testProduct;

	@BeforeEach
	void setUp() {
		System.out.println("Executing @BeforeEach: Setting up fake product data...");
		testProduct = new Product();
		testProduct.setId(1L);
		testProduct.setName("Gaming Laptop");
		testProduct.setDescription("High end gaming laptop");
		testProduct.setPrice(new BigDecimal("1200.00"));
		testProduct.setStock(10);
		testProduct.setCategory("Electronics");
	}

	@DisplayName("Test Adding a Product")
	@Test
	void testAddProduct() {
		// Arrange
		when(productRepository.save(any(Product.class))).thenReturn(testProduct);

		// Act
		Product savedProduct = productService.addProduct(testProduct);

		// Assert
		assertNotNull(savedProduct);
		assertEquals("Gaming Laptop", savedProduct.getName());
		assertEquals(new BigDecimal("1200.00"), savedProduct.getPrice());

		// Verify that the save method was actually called exactly once
		verify(productRepository, times(1)).save(any(Product.class));
	}

	@DisplayName("Test Getting a Product by ID (Success)")
	@Test
	void testGetProductById_Success() {
		// Arrange
		when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

		// Act
		Product foundProduct = productService.getProductById(1L);

		// Assert
		assertNotNull(foundProduct);
		assertEquals(1L, foundProduct.getId());
		assertEquals("Gaming Laptop", foundProduct.getName());
	}

	@DisplayName("Test Getting a Product by ID (Not Found Exception)")
	@Test
	void testGetProductById_NotFound() {
		// Arrange: Simulate the database returning empty
		when(productRepository.findById(2L)).thenReturn(Optional.empty());

		// Act & Assert: Check if asking for a non-existent product throws
		// CustomException
		assertThrows(CustomException.class, () -> {
			productService.getProductById(2L);
		});
	}

	@DisplayName("Test Getting All Products (No Filters)")
	@Test
	void testGetAllProducts_NoFilters() {
		// Arrange
		Page<Product> dummyPage = new PageImpl<>(Arrays.asList(testProduct));
		when(productRepository.findAll(any(Pageable.class))).thenReturn(dummyPage);

		// Act: Passing null for category and maxPrice triggers the findAll() path
		Page<Product> products = productService.getAllProducts(0, 10, "id", null, null);

		// Assert
		assertNotNull(products);
		assertEquals(1, products.getContent().size());
		verify(productRepository, times(1)).findAll(any(Pageable.class));
	}

	@DisplayName("Test Getting All Products (Filtered by Category)")
	@Test
	void testGetAllProducts_ByCategory() {
		// Arrange
		Page<Product> dummyPage = new PageImpl<>(Arrays.asList(testProduct));
		when(productRepository.findByCategory(any(String.class), any(Pageable.class))).thenReturn(dummyPage);

		// Act: Passing "Electronics" as category triggers the findByCategory path
		Page<Product> products = productService.getAllProducts(0, 10, "id", "Electronics", null);

		// Assert
		assertNotNull(products);
		assertEquals(1, products.getContent().size());
		verify(productRepository, times(1)).findByCategory(any(String.class), any(Pageable.class));
	}

	@DisplayName("Test Getting All Products (Filtered by Max Price)")
	@Test
	void testGetAllProducts_ByMaxPrice() {
		// Arrange
		Page<Product> dummyPage = new PageImpl<>(Arrays.asList(testProduct));
		when(productRepository.findByPriceLessThanEqual(any(BigDecimal.class), any(Pageable.class)))
				.thenReturn(dummyPage);

		// Act: Passing null for category but providing a max price triggers
		// findByPriceLessThanEqual
		Page<Product> products = productService.getAllProducts(0, 10, "id", null, new BigDecimal("2000.00"));

		// Assert
		assertNotNull(products);
		assertEquals(1, products.getContent().size());
		verify(productRepository, times(1)).findByPriceLessThanEqual(any(BigDecimal.class), any(Pageable.class));
	}

	@DisplayName("Test Updating a Product (Success)")
	@Test
	void testUpdateProduct_Success() {
		// Arrange
		Product updatedDetails = new Product();
		updatedDetails.setName("Upgraded Gaming Laptop");
		updatedDetails.setPrice(new BigDecimal("1500.00"));
		updatedDetails.setStock(20);

		when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
		when(productRepository.save(any(Product.class))).thenReturn(updatedDetails);

		// Act
		Product result = productService.updateProduct(1L, updatedDetails);

		// Assert
		assertNotNull(result);
		assertEquals("Upgraded Gaming Laptop", result.getName());
		assertEquals(new BigDecimal("1500.00"), result.getPrice());
		assertEquals(20, result.getStock());

		verify(productRepository, times(1)).findById(1L);
		verify(productRepository, times(1)).save(any(Product.class));
	}

	@DisplayName("Test Updating a Product (Not Found)")
	@Test
	void testUpdateProduct_NotFound() {
		// Arrange
		Product updatedDetails = new Product();
		updatedDetails.setName("Upgraded Gaming Laptop");

		when(productRepository.findById(2L)).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(CustomException.class, () -> {
			productService.updateProduct(2L, updatedDetails);
		});

		// Verify that save() was never called
		verify(productRepository, times(0)).save(any(Product.class));
	}

	@DisplayName("Test Deleting a Product")
	@Test
	void testDeleteProduct() {
		// Act
		productService.deleteProduct(1L);

		// Assert
		verify(productRepository, times(1)).deleteById(1L);
	}
}