package com.incture.ecommerceBackend.Controller;

import java.security.Principal;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.incture.ecommerceBackend.DTO.OrderResponseDTO;
import com.incture.ecommerceBackend.Entity.Order;
import com.incture.ecommerceBackend.Service.EmailService;
import com.incture.ecommerceBackend.Service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

	// Setting up the Logger
	private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
	// Logger is used to print important events in logs.

	private final OrderService orderService;
	// Handles the main order business logic

	private final ModelMapper modelMapper;
	// Converts Entity objects to DTO objects

	private final EmailService emailService;
	// Sends confirmation emails after successful orders

	// Constructor Injection!
	@Autowired
	public OrderController(OrderService orderService, ModelMapper modelMapper, EmailService emailService) {
		this.orderService = orderService;
		this.modelMapper = modelMapper;
		this.emailService = emailService;
	}

	@PostMapping("/checkout")
	public ResponseEntity<OrderResponseDTO> checkout(Principal principal) {
		String email = principal.getName(); // Retrieves the email of the currently logged-in user

		// Logging the start of the event
		logger.info("Checkout process initiated by user: {}", email);

		Order rawOrder = orderService.checkout(email); // Calls OrderService to create the order from the user's cart.

		// Logging the result
		if (rawOrder.getPaymentStatus().equals("SUCCESS")) {
			logger.info("Order successfully placed with ID: {}. Total: {}", rawOrder.getId(),
					rawOrder.getTotalAmount());

			// Sends confirmation email to the user
			emailService.sendOrderConfirmation(email, rawOrder.getId(), rawOrder.getTotalAmount().toString());
		} else {
			logger.warn("Order checkout failed due to payment simulation for user: {}", email);
		}

		// Converting the Entity into the DTO!
		OrderResponseDTO cleanResponse = modelMapper.map(rawOrder, OrderResponseDTO.class);

		return ResponseEntity.ok(cleanResponse); // Returns HTTP 200 response with order details.
	}

	@GetMapping
	public ResponseEntity<List<Order>> getOrderHistory(Principal principal) {
		return ResponseEntity.ok(orderService.getUserOrders(principal.getName()));
		// Fetches all orders belonging to the logged-in user
	}

	@GetMapping("/{id}")
	public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
		return ResponseEntity.ok(orderService.getOrderById(id));
		// Retrieves a specific order using its id
	}

	@PutMapping("/{id}/status")
	public ResponseEntity<Order> updateStatus(@PathVariable Long id, @RequestParam String status) {
		return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
		// Updates the order status
	}
}