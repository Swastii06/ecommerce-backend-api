package com.incture.ecommerceBackend.Entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne // A single user can have muultiple orders
	@JoinColumn(name = "user_id")
	private User user;

	@Column(name = "total_amount")
	private BigDecimal totalAmount;

	@Column(name = "order_date")
	private LocalDateTime orderDate;

	@Column(name = "payment_status")
	private String paymentStatus;

	@Column(name = "order_status")
	private String orderStatus;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
	private List<OrderItem> orderItems;

	public Long getId() {
		return id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public LocalDateTime getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(LocalDateTime orderDate) {
		this.orderDate = orderDate;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public List<OrderItem> getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(List<OrderItem> orderItems) {
		this.orderItems = orderItems;
	}

	public Order(User user, BigDecimal totalAmount, LocalDateTime orderDate, String paymentStatus, String orderStatus,
			List<OrderItem> orderItems) {
		super();
		this.user = user;
		this.totalAmount = totalAmount;
		this.orderDate = orderDate;
		this.paymentStatus = paymentStatus;
		this.orderStatus = orderStatus;
		this.orderItems = orderItems;
	}

	public Order() {
		super();
	}

}