package com.incture.ecommerceBackend.Entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class CartItemTest {
	@Test
	public void testGettersAndSetters() {
		CartItem item = new CartItem();
		item.setQuantity(3);

		assertEquals(3, item.getQuantity());
	}
}