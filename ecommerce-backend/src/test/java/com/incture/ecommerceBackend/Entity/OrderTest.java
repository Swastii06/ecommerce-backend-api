package com.incture.ecommerceBackend.Entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class OrderTest {
	@Test
	public void testGettersAndSetters() {
		Order order = new Order();
		order.setTotalAmount(new BigDecimal("2400.00"));
		order.setPaymentStatus("SUCCESS");
		order.setOrderStatus("SHIPPED");
		order.setOrderDate(LocalDateTime.now());

		assertEquals(new BigDecimal("2400.00"), order.getTotalAmount());
		assertEquals("SUCCESS", order.getPaymentStatus());
		assertEquals("SHIPPED", order.getOrderStatus());
		assertNotNull(order.getOrderDate());
	}
}