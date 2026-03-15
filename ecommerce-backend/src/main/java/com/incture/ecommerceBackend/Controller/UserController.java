package com.incture.ecommerceBackend.Controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.incture.ecommerceBackend.Entity.User;
import com.incture.ecommerceBackend.Service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;
	private final PasswordEncoder passwordEncoder;

	@Autowired
	public UserController(UserService userService, PasswordEncoder passwordEncoder) {
		this.userService = userService;
		this.passwordEncoder = passwordEncoder;
	}

	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestBody User user) {
		// Encrypting the password before saving
		user.setPassword(passwordEncoder.encode(user.getPassword()));

		// If the user didn't provide a role, make them a CUSTOMER by default
		if (user.getRole() == null || user.getRole().isEmpty()) {
			user.setRole("CUSTOMER");
		}

		userService.save(user);
		return ResponseEntity.ok("User registered successfully");
	}

	// Profile Update (Logged-in user)
	@PutMapping("/profile")
	public ResponseEntity<User> updateProfile(@RequestBody User user, Principal principal) {
		return ResponseEntity.ok(userService.updateProfile(principal.getName(), user));
		// Uses the logged-in user's email to update their profile
	}

	// Change Password (Logged-in user)
	@PutMapping("/change-password")
	public ResponseEntity<String> changePassword(@RequestParam String newPassword, Principal principal) {
		userService.changePassword(principal.getName(), newPassword, passwordEncoder);
		return ResponseEntity.ok("Password changed successfully!");
	}

	// Admin gets user by id
	@GetMapping("/{id}")
	public ResponseEntity<User> getUserById(@PathVariable Long id) {
		return ResponseEntity.ok(userService.getUserById(id));
	}

	// Admin updates user
	@PutMapping("/{id}")
	public ResponseEntity<User> updateUserByAdmin(@PathVariable Long id, @RequestBody User user) {
		return ResponseEntity.ok(userService.updateUserByAdmin(id, user));
	}

	// Admin deletes User
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteUser(@PathVariable Long id) {
		userService.deleteUser(id);
		return ResponseEntity.ok("User deleted successfully!");
	}
}