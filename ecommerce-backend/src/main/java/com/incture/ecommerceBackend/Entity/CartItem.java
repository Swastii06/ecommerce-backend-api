package com.incture.ecommerceBackend.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "cart_items")
public class CartItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne // One cart can has many CartItems
	@JoinColumn(name = "cart_id")
	@JsonIgnore
	private Cart cart;

	@ManyToOne // Many people can put the exact same product into their individual carts
				// or orders
	@JoinColumn(name = "product_id")
	private Product product;

	private Integer quantity;

	public Long getId() {
		return id;
	}

	public Cart getCart() {
		return cart;
	}

	public void setCart(Cart cart) {
		this.cart = cart;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public CartItem(Cart cart, Product product, Integer quantity) {
		super();
		this.cart = cart;
		this.product = product;
		this.quantity = quantity;
	}

	public CartItem() {
		super();
	}

}