package com.incture.ecommerceBackend.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
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
import com.incture.ecommerceBackend.Entity.Order;
import com.incture.ecommerceBackend.Entity.Product;
import com.incture.ecommerceBackend.Entity.User;
import com.incture.ecommerceBackend.Exception.CustomException;
import com.incture.ecommerceBackend.Repository.CartRepository;
import com.incture.ecommerceBackend.Repository.OrderRepository;
import com.incture.ecommerceBackend.Repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private CartService cartService;

	@Mock
	private ProductRepository productRepository;

	@Mock
	private CartRepository cartRepository;

	@InjectMocks
	private OrderService orderService;

	private User testUser;
	private Product testProduct;
	private Cart testCart;

	@BeforeEach
	void setUp() {
		testUser = new User();
		testUser.setEmail("user@example.com");

		testProduct = new Product();
		testProduct.setId(1L);
		testProduct.setName("Smartphone");
		testProduct.setPrice(new BigDecimal("500.00"));
		testProduct.setStock(5);

		testCart = new Cart();
		testCart.setUser(testUser);
		testCart.setTotalPrice(new BigDecimal("500.00"));
		testCart.setCartItems(new ArrayList<>());

		CartItem item = new CartItem();
		item.setProduct(testProduct);
		item.setQuantity(1);
		testCart.getCartItems().add(item);
	}

	@DisplayName("Test Checkout - Empty Cart Error")
	@Test
	void testCheckout_EmptyCart() {
		// Arrange: Make the cart empty
		testCart.getCartItems().clear();
		when(cartService.getCartByUserEmail("user@example.com")).thenReturn(testCart);

		// Act & Assert
		CustomException ex = assertThrows(CustomException.class, () -> {
			orderService.checkout("user@example.com");
		});
		assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
	}

	@DisplayName("Test Checkout - Out of Stock Error")
	@Test
	void testCheckout_OutOfStock() {
		// Arrange: Set cart quantity to 10, but stock is only 5
		testCart.getCartItems().get(0).setQuantity(10);
		when(cartService.getCartByUserEmail("user@example.com")).thenReturn(testCart);

		// Act & Assert
		CustomException ex = assertThrows(CustomException.class, () -> {
			orderService.checkout("user@example.com");
		});
		assertEquals(HttpStatus.CONFLICT, ex.getStatus());
	}

	@DisplayName("Test Checkout - Success Flow")
	@Test
	void testCheckout_Success() {
		// Arrange
		when(cartService.getCartByUserEmail("user@example.com")).thenReturn(testCart);
		when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);

		// Act
		Order order = orderService.checkout("user@example.com");

		// Assert
		assertNotNull(order);
		if ("SUCCESS".equals(order.getPaymentStatus())) {
			assertEquals("PLACED", order.getOrderStatus());
			// Verify stock was reduced and saved
			verify(productRepository, atLeastOnce()).save(any(Product.class));
			// Verify cart was cleared and saved
			verify(cartRepository, times(1)).save(any(Cart.class));
		} else {
			assertEquals("FAILED", order.getPaymentStatus());
			assertEquals("CANCELLED", order.getOrderStatus());
		}
	}

	@DisplayName("Test Get Order By ID - Success")
	@Test
	void testGetOrderById_Success() {
		// Mock the Order object so we don't need a setter for ID
		Order order = org.mockito.Mockito.mock(Order.class);
		when(order.getId()).thenReturn(100L);

		when(orderRepository.findById(100L)).thenReturn(Optional.of(order));

		Order result = orderService.getOrderById(100L);

		assertNotNull(result);
		assertEquals(100L, result.getId());
	}

	@DisplayName("Test Update Order Status")
	@Test
	void testUpdateOrderStatus() {
		// Create a real Order entity if possible, or a mock
		Order order = new Order();
		// If setId doesn't work, just rely on the mock returning this object

		// Mock the behavior of findById (which is called by getOrderById)
		when(orderRepository.findById(100L)).thenReturn(Optional.of(order));

		// Mock the save behavior
		when(orderRepository.save(any(Order.class))).thenReturn(order);

		// Act
		Order updatedOrder = orderService.updateOrderStatus(100L, "SHIPPED");

		// Assert
		assertNotNull(updatedOrder);
		assertEquals("SHIPPED", updatedOrder.getOrderStatus());

		// Verify the interactions
		verify(orderRepository).findById(100L);
		verify(orderRepository).save(any(Order.class));
	}

	@Test
	@DisplayName("Test Get User Orders")
	void testGetUserOrders() {
		// Arrange
		String email = "test@example.com";
		com.incture.ecommerceBackend.Entity.User mockUser = new com.incture.ecommerceBackend.Entity.User();
		mockUser.setEmail(email);

		com.incture.ecommerceBackend.Entity.Cart mockCart = new com.incture.ecommerceBackend.Entity.Cart();
		mockCart.setUser(mockUser);

		when(cartService.getCartByUserEmail(email)).thenReturn(mockCart);
		when(orderRepository.findByUser(mockUser)).thenReturn(new java.util.ArrayList<>());

		// Act
		java.util.List<com.incture.ecommerceBackend.Entity.Order> result = orderService.getUserOrders(email);

		// Assert
		org.junit.jupiter.api.Assertions.assertNotNull(result);
		verify(orderRepository, times(1)).findByUser(mockUser);
	}

	@Test
	@DisplayName("Test Checkout - Guaranteed Success Path")
	void testCheckout_SuccessPath() {
		// Arrange
		String email = "test@example.com";
		com.incture.ecommerceBackend.Entity.Cart mockCart = new com.incture.ecommerceBackend.Entity.Cart();
		mockCart.setUser(new com.incture.ecommerceBackend.Entity.User());

		com.incture.ecommerceBackend.Entity.CartItem item = new com.incture.ecommerceBackend.Entity.CartItem();
		com.incture.ecommerceBackend.Entity.Product product = new com.incture.ecommerceBackend.Entity.Product();
		product.setStock(10); // Plentiful stock to pass the check
		product.setPrice(new java.math.BigDecimal("100"));
		item.setProduct(product);
		item.setQuantity(1);

		mockCart.setCartItems(new java.util.ArrayList<>(java.util.List.of(item)));
		mockCart.setTotalPrice(new java.math.BigDecimal("100"));

		when(cartService.getCartByUserEmail(email)).thenReturn(mockCart);
		when(orderRepository.save(any(com.incture.ecommerceBackend.Entity.Order.class)))
				.thenAnswer(i -> i.getArguments()[0]);

		// Act - We loop up to 10 times to guarantee we beat the Math.random() 20%
		// failure rate
		com.incture.ecommerceBackend.Entity.Order result = null;
		for (int i = 0; i < 10; i++) {
			result = orderService.checkout(email);
			if ("SUCCESS".equals(result.getPaymentStatus())) {
				break; // We hit the success block, exit the loop!
			}
		}

		// Assert
		org.junit.jupiter.api.Assertions.assertNotNull(result);
		org.junit.jupiter.api.Assertions.assertEquals("SUCCESS", result.getPaymentStatus());
		verify(productRepository, atLeastOnce()).save(any(com.incture.ecommerceBackend.Entity.Product.class));
		verify(cartRepository, atLeastOnce()).save(any(com.incture.ecommerceBackend.Entity.Cart.class));
	}
}