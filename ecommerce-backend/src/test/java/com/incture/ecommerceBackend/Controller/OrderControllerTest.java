package com.incture.ecommerceBackend.Controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.incture.ecommerceBackend.DTO.OrderResponseDTO;
import com.incture.ecommerceBackend.Entity.Order;
import com.incture.ecommerceBackend.Service.EmailService;
import com.incture.ecommerceBackend.Service.OrderService;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private OrderService orderService;

	@MockitoBean
	private ModelMapper modelMapper;

	@MockitoBean
	private EmailService emailService;

	private Order testOrder;
	private OrderResponseDTO testResponseDTO;

	@BeforeEach
	void setUp() {
		testOrder = new Order();

		testOrder.setTotalAmount(new BigDecimal("1500.00"));
		testOrder.setPaymentStatus("SUCCESS");

		testResponseDTO = new OrderResponseDTO();
		testResponseDTO.setTotalAmount(new BigDecimal("1500.00"));
		testResponseDTO.setPaymentStatus("SUCCESS");
	}

	@DisplayName("POST /api/orders/checkout - Success Flow")
	@Test
	void testCheckout_Success() throws Exception {
		// Arrange
		when(orderService.checkout("test@example.com")).thenReturn(testOrder);
		when(modelMapper.map(any(Order.class), any())).thenReturn(testResponseDTO);

		// Act & Assert
		mockMvc.perform(post("/api/orders/checkout").principal(() -> "test@example.com")).andExpect(status().isOk())
				.andExpect(jsonPath("$.paymentStatus").value("SUCCESS"));

		// Verify email was sent
		verify(emailService, times(1)).sendOrderConfirmation(anyString(), any(), anyString());
	}

	@DisplayName("POST /api/orders/checkout - Payment Failed")
	@Test
	void testCheckout_PaymentFailed() throws Exception {
		// Arrange
		testOrder.setPaymentStatus("FAILED");
		testResponseDTO.setPaymentStatus("FAILED");

		when(orderService.checkout("test@example.com")).thenReturn(testOrder);
		when(modelMapper.map(any(Order.class), any())).thenReturn(testResponseDTO);

		// Act
		mockMvc.perform(post("/api/orders/checkout").principal(() -> "test@example.com")).andExpect(status().isOk())
				.andExpect(jsonPath("$.paymentStatus").value("FAILED"));

		// Verify email was never sent on failure
		verify(emailService, never()).sendOrderConfirmation(anyString(), anyLong(), anyString());
	}

	@DisplayName("GET /api/orders - Get History")
	@Test
	void testGetOrderHistory() throws Exception {
		when(orderService.getUserOrders("test@example.com")).thenReturn(Arrays.asList(testOrder));

		mockMvc.perform(get("/api/orders").principal(() -> "test@example.com")).andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(1));
	}
}