package com.incture.ecommerceBackend.Entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

public class CartTest {
	@Test
	public void testGettersAndSetters() {
		Cart cart = new Cart();
		cart.setTotalPrice(new BigDecimal("500.00"));

		assertEquals(new BigDecimal("500.00"), cart.getTotalPrice());
	}
}