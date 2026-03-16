package com.incture.ecommerceBackend.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.incture.ecommerceBackend.Entity.Cart;
import com.incture.ecommerceBackend.Entity.CartItem;
import com.incture.ecommerceBackend.Entity.Product;
import com.incture.ecommerceBackend.Entity.User;
import com.incture.ecommerceBackend.Exception.CustomException;
import com.incture.ecommerceBackend.Repository.CartRepository;
import com.incture.ecommerceBackend.Repository.ProductRepository;
import com.incture.ecommerceBackend.Repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

	@Mock
	private CartRepository cartRepository;

	@Mock
	private ProductRepository productRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private CartService cartService;

	private User testUser;
	private Product testProduct;
	private Cart testCart;

	@BeforeEach
	void setUp() {
		testUser = new User();
		testUser.setEmail("test@example.com");

		testProduct = new Product();
		testProduct.setId(1L);
		testProduct.setName("Laptop");
		testProduct.setPrice(new BigDecimal("1000.00"));
		testProduct.setStock(10);

		testCart = new Cart();
		testCart.setUser(testUser);
		testCart.setCartItems(new ArrayList<>());
		testCart.setTotalPrice(BigDecimal.ZERO);
	}

	@DisplayName("Test Get Cart By User Email - Existing Cart")
	@Test
	void testGetCartByUserEmail_Existing() {
		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
		when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));

		Cart result = cartService.getCartByUserEmail("test@example.com");

		assertNotNull(result);
		assertEquals("test@example.com", result.getUser().getEmail());
	}

	@DisplayName("Test Add Product to Cart - Success")
	@Test
	void testAddProductToCart_Success() {
		// Arrange
		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
		when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
		when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
		when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

		// Act
		Cart result = cartService.addProductToCart("test@example.com", 1L, 2);

		// Assert
		assertNotNull(result);
		assertEquals(1, result.getCartItems().size());
		assertEquals(new BigDecimal("2000.00"), result.getTotalPrice());
		verify(cartRepository, times(1)).save(any(Cart.class));
	}

	@DisplayName("Test Add Product to Cart - Insufficient Stock")
	@Test
	void testAddProductToCart_InsufficientStock() {
		// Arrange
		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
		when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
		when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

		// Act & Assert
		CustomException exception = assertThrows(CustomException.class, () -> {
			cartService.addProductToCart("test@example.com", 1L, 50); // Asking for 50, only 10 in stock
		});

		assertEquals(HttpStatus.CONFLICT, exception.getStatus());
		verify(cartRepository, times(0)).save(any(Cart.class));
	}

	@DisplayName("Test Update Cart Quantity")
	@Test
	void testUpdateCartQuantity_Success() {
		// Arrange: Set up cart with an existing item
		CartItem item = new CartItem();
		item.setProduct(testProduct);
		item.setQuantity(1);
		testCart.getCartItems().add(item);

		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
		when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
		when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

		// Act
		Cart result = cartService.updateCartQuantity("test@example.com", 1L, 5);

		// Assert
		assertEquals(5, result.getCartItems().get(0).getQuantity());
		assertEquals(new BigDecimal("5000.00"), result.getTotalPrice());
	}

	@DisplayName("Test Remove Product From Cart")
	@Test
	void testRemoveProductFromCart() {
		// Arrange
		CartItem item = new CartItem();
		item.setProduct(testProduct);
		testCart.getCartItems().add(item);

		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
		when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
		when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

		// Act
		Cart result = cartService.removeProductFromCart("test@example.com", 1L);

		// Assert
		assertEquals(0, result.getCartItems().size());
		assertEquals(BigDecimal.ZERO, result.getTotalPrice());
	}
}