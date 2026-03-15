package com.incture.ecommerceBackend.Entity;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "carts")
public class Cart {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne // One user gets one cart
	@JoinColumn(name = "user_id")
	@JsonIgnore // It tells Postman to stop reading and just print the data it has.
	private User user;

	@Column(name = "total_price")
	private BigDecimal totalPrice;

	@OneToMany(mappedBy = "cart", cascade = CascadeType.ALL) // One cart can has many CartItems, cascade =
																// CascadeType.ALL means if a User deletes their entire
																// Cart, MySQL will automatically delete all the
																// CartItems inside it too
	private List<CartItem> cartItems;

	public Long getId() {
		return id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public List<CartItem> getCartItems() {
		return cartItems;
	}

	public void setCartItems(List<CartItem> cartItems) {
		this.cartItems = cartItems;
	}

	public Cart(Long id, User user, BigDecimal totalPrice, List<CartItem> cartItems) {
		super();
		this.user = user;
		this.totalPrice = totalPrice;
		this.cartItems = cartItems;
	}

	public Cart() {
		super();
	}

}