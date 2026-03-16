package com.incture.ecommerceBackend.Controller;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.incture.ecommerceBackend.Entity.Cart;
import com.incture.ecommerceBackend.Entity.User;
import com.incture.ecommerceBackend.Service.CartService;

@WebMvcTest(CartController.class)
@AutoConfigureMockMvc(addFilters = false)
class CartControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private CartService cartService;

	private Cart testCart;

	@BeforeEach
	void setUp() {
		// 1. Create a REAL User object
		User testUser = new User();
		testUser.setEmail("test@example.com");
		testUser.setName("Test User");

		// 2. Create a REAL Cart object
		testCart = new Cart();
		testCart.setUser(testUser);
		testCart.setCartItems(new ArrayList<>());
		testCart.setTotalPrice(java.math.BigDecimal.ZERO);
	}

	@DisplayName("GET /api/cart - Get User Cart")
	@Test
	void testGetCart() throws Exception {
		// Arrange
		when(cartService.getCartByUserEmail("test@example.com")).thenReturn(testCart);

		// Act & Assert
		mockMvc.perform(get("/api/cart").principal(() -> "test@example.com")).andExpect(status().isOk())
				// We test the Total Price instead of the hidden User email
				.andExpect(jsonPath("$.totalPrice").value(0.0));
	}

	@DisplayName("POST /api/cart/add/{productId} - Add Item")
	@Test
	void testAddToCart() throws Exception {
		when(cartService.addProductToCart(anyString(), anyLong(), anyInt())).thenReturn(testCart);

		mockMvc.perform(post("/api/cart/add/1").param("quantity", "2").principal(() -> "test@example.com"))
				.andExpect(status().isOk());
	}

	@DisplayName("PUT /api/cart/update/{productId} - Update Quantity")
	@Test
	void testUpdateQuantity() throws Exception {
		when(cartService.updateCartQuantity(anyString(), anyLong(), anyInt())).thenReturn(testCart);

		mockMvc.perform(put("/api/cart/update/1").param("quantity", "5").principal(() -> "test@example.com"))
				.andExpect(status().isOk());
	}

	@DisplayName("DELETE /api/cart/remove/{productId} - Remove Item")
	@Test
	void testRemoveFromCart() throws Exception {
		when(cartService.removeProductFromCart(anyString(), anyLong())).thenReturn(testCart);

		mockMvc.perform(delete("/api/cart/remove/1").principal(() -> "test@example.com")).andExpect(status().isOk());
	}
}