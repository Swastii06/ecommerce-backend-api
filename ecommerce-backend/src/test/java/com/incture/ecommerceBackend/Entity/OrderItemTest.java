package com.incture.ecommerceBackend.Entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

public class OrderItemTest {
	@Test
	public void testGettersAndSetters() {
		OrderItem item = new OrderItem();
		item.setQuantity(2);
		item.setPrice(new BigDecimal("1200.00"));

		assertEquals(2, item.getQuantity());
		assertEquals(new BigDecimal("1200.00"), item.getPrice());
	}
}