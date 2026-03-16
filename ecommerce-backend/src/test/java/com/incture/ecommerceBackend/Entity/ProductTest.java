package com.incture.ecommerceBackend.Entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

public class ProductTest {

	@Test
	public void testGettersAndSetters() {
		Product product = new Product();
		product.setId(100L);
		product.setName("Gaming Laptop");
		product.setPrice(new BigDecimal("1500.00"));
		product.setStock(25);
		product.setCategory("Electronics");

		assertEquals(100L, product.getId());
		assertEquals("Gaming Laptop", product.getName());
		assertEquals(new BigDecimal("1500.00"), product.getPrice());
		assertEquals(25, product.getStock());
		assertEquals("Electronics", product.getCategory());
	}
}