package com.incture.ecommerceBackend.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.incture.ecommerceBackend.Entity.Cart;
import com.incture.ecommerceBackend.Entity.CartItem;
import com.incture.ecommerceBackend.Entity.Product;
import com.incture.ecommerceBackend.Entity.User;
import com.incture.ecommerceBackend.Exception.CustomException;
import com.incture.ecommerceBackend.Repository.CartRepository;
import com.incture.ecommerceBackend.Repository.ProductRepository;
import com.incture.ecommerceBackend.Repository.UserRepository;

@Service
public class CartService {

	private final CartRepository cartRepository;
	private final ProductRepository productRepository;
	private final UserRepository userRepository;

	@Autowired
	public CartService(CartRepository cartRepository, ProductRepository productRepository,
			UserRepository userRepository) {
		this.cartRepository = cartRepository;
		this.productRepository = productRepository;
		this.userRepository = userRepository;
	}

	// Fetch the cart of the currently logged-in user
	public Cart getCartByUserEmail(String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "User not found"));

		return cartRepository.findByUser(user).orElseGet(() -> {
			Cart newCart = new Cart();
			newCart.setUser(user);
			newCart.setTotalPrice(BigDecimal.ZERO);
			newCart.setCartItems(new ArrayList<>());
			return cartRepository.save(newCart);
		});
	}

	// Add item to cart
	public Cart addProductToCart(String email, Long productId, int quantity) {
		Cart cart = getCartByUserEmail(email);
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Product not found"));

		// Condition is: Prevent ordering if stock is less than requested quantity
		if (product.getStock() < quantity) {
			throw new CustomException(HttpStatus.CONFLICT,
					"Not enough stock available! Only " + product.getStock() + " left.");
		}

		// Check if item is already in the cart
		Optional<CartItem> existingItem = cart.getCartItems().stream()
				.filter(item -> item.getProduct().getId().equals(productId)).findFirst();

		if (existingItem.isPresent()) {
			existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
		} else {
			CartItem newItem = new CartItem();
			newItem.setCart(cart);
			newItem.setProduct(product);
			newItem.setQuantity(quantity);
			cart.getCartItems().add(newItem);
		}

		recalculateTotal(cart);
		return cartRepository.save(cart);
	}

	// Update item quantity
	public Cart updateCartQuantity(String email, Long productId, int newQuantity) {
		Cart cart = getCartByUserEmail(email);
		CartItem item = cart.getCartItems().stream().filter(ci -> ci.getProduct().getId().equals(productId)).findFirst()
				.orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Item not found in cart"));

		if (item.getProduct().getStock() < newQuantity) {
			throw new CustomException(HttpStatus.CONFLICT, "Not enough stock available!");
		}

		item.setQuantity(newQuantity);
		recalculateTotal(cart);
		return cartRepository.save(cart);
	}

	// Remove item from cart
	public Cart removeProductFromCart(String email, Long productId) {
		Cart cart = getCartByUserEmail(email);
		cart.getCartItems().removeIf(item -> item.getProduct().getId().equals(productId));
		recalculateTotal(cart);
		return cartRepository.save(cart);
	}

	// Calculate total price
	private void recalculateTotal(Cart cart) {
		BigDecimal total = cart.getCartItems().stream()
				.map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		cart.setTotalPrice(total);
	}
}