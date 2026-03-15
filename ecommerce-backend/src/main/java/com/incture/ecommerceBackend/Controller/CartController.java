package com.incture.ecommerceBackend.Controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.incture.ecommerceBackend.Entity.Cart;
import com.incture.ecommerceBackend.Service.CartService;

@RestController
@RequestMapping("/api/cart")
public class CartController {

	private final CartService cartService;

	@Autowired
	public CartController(CartService cartService) {
		this.cartService = cartService;
	}

	// Get current user's cart
	@GetMapping
	public ResponseEntity<Cart> getCart(Principal principal) {

		return ResponseEntity.ok(cartService.getCartByUserEmail(principal.getName()));
		// principal.getName() gives the logged-in user's email
		// cartService fetches the cart of that user
		// ResponseEntity.ok() returns HTTP status 200 with cart data.
	}

	// Adds a product to cart
	@PostMapping("/add/{productId}")
	public ResponseEntity<Cart> addToCart(@PathVariable Long productId, @RequestParam(defaultValue = "1") int quantity,
			Principal principal) { // Extracts quantity from request parameters and if quantity is not provided,
									// default value = 1.
		return ResponseEntity.ok(cartService.addProductToCart(principal.getName(), productId, quantity));
		// Calls service method to add product to the user's cart.
		// Returns updated cart as response.
	}

	// Updates quantity of a product in cart
	@PutMapping("/update/{productId}")
	public ResponseEntity<Cart> updateQuantity(@PathVariable Long productId, @RequestParam int quantity,
			Principal principal) {
		return ResponseEntity.ok(cartService.updateCartQuantity(principal.getName(), productId, quantity));
		// Calls service method to update product quantity.
		// Returns updated cart.
	}

	// Removes a product from cart
	@DeleteMapping("/remove/{productId}")
	public ResponseEntity<Cart> removeFromCart(@PathVariable Long productId, Principal principal) {
		return ResponseEntity.ok(cartService.removeProductFromCart(principal.getName(), productId));
		// Calls service method to remove the product from user's cart
		// Returns updated cart
	}
}