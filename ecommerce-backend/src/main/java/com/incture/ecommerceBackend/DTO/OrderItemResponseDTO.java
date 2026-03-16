package com.incture.ecommerceBackend.DTO;

import java.math.BigDecimal;

public class OrderItemResponseDTO {
	private String productName;
	private int quantity;
	private BigDecimal price;

	// Getters and Setters
	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}
}