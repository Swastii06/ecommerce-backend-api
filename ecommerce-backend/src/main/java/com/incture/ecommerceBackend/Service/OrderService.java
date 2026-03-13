package com.incture.ecommerceBackend.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.incture.ecommerceBackend.Entity.Cart;
import com.incture.ecommerceBackend.Entity.CartItem;
import com.incture.ecommerceBackend.Entity.Order;
import com.incture.ecommerceBackend.Entity.OrderItem;
import com.incture.ecommerceBackend.Entity.Product;
import com.incture.ecommerceBackend.Exception.CustomException;
import com.incture.ecommerceBackend.Repository.CartRepository;
import com.incture.ecommerceBackend.Repository.OrderRepository;
import com.incture.ecommerceBackend.Repository.ProductRepository;

@Service
public class OrderService {

	private final OrderRepository orderRepository;
	private final CartService cartService;
	private final ProductRepository productRepository;
	private final CartRepository cartRepository;

	@Autowired
	public OrderService(OrderRepository orderRepository, CartService cartService, ProductRepository productRepository,
			CartRepository cartRepository) {
		this.orderRepository = orderRepository;
		this.cartService = cartService;
		this.productRepository = productRepository;
		this.cartRepository = cartRepository;
	}

	public Order checkout(String email) {
		Cart cart = cartService.getCartByUserEmail(email);

		if (cart.getCartItems().isEmpty()) {
			throw new CustomException("Cart is empty!");
		}

		// Initialize Order
		Order order = new Order();
		order.setUser(cart.getUser());
		order.setOrderDate(LocalDateTime.now());
		order.setTotalAmount(cart.getTotalPrice());
		order.setOrderItems(new ArrayList<>());

		// Convert CartItems to OrderItems
		for (CartItem cartItem : cart.getCartItems()) {
			if (cartItem.getProduct().getStock() < cartItem.getQuantity()) {
				throw new CustomException("Product " + cartItem.getProduct().getName() + " is out of stock!");
			}

			OrderItem orderItem = new OrderItem();
			orderItem.setOrder(order);
			orderItem.setProduct(cartItem.getProduct());
			orderItem.setQuantity(cartItem.getQuantity());
			orderItem.setPrice(cartItem.getProduct().getPrice());
			order.getOrderItems().add(orderItem);
		}

		// Simulating Payment here- 80% chance of Success, 20% chance of Failure
		boolean paymentSuccess = Math.random() > 0.2;

		if (paymentSuccess) {
			order.setPaymentStatus("SUCCESS");
			order.setOrderStatus("PLACED");

			// Reduce stock after successful order
			for (OrderItem item : order.getOrderItems()) {
				Product product = item.getProduct();
				product.setStock(product.getStock() - item.getQuantity());
				productRepository.save(product);
			}

			// Clear the cart
			cart.getCartItems().clear();
			cart.setTotalPrice(java.math.BigDecimal.ZERO);
			cartRepository.save(cart);

		} else {
			order.setPaymentStatus("FAILED");
			order.setOrderStatus("CANCELLED");
		}

		return orderRepository.save(order);
	}

	public List<Order> getUserOrders(String email) {
		Cart cart = cartService.getCartByUserEmail(email);
		return orderRepository.findByUser(cart.getUser());
	}

	public Order getOrderById(Long id) {
		return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
	}

	public Order updateOrderStatus(Long orderId, String newStatus) {
		Order order = getOrderById(orderId);
		order.setOrderStatus(newStatus);
		return orderRepository.save(order);
	}
}