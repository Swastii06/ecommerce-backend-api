package com.incture.ecommerceBackend.Utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

class JwtUtilTest {

	private JwtUtil jwtUtil;
	private UserDetails userDetails;

	@BeforeEach
	void setUp() {
		jwtUtil = new JwtUtil();
		// Creating a simple Spring Security User object for testing
		userDetails = new User("swasti@example.com", "password", new ArrayList<>());
	}

	@DisplayName("Test Generate Token")
	@Test
	void testGenerateToken() {
		// Adding 3600000 (1 hour) as the second argument
		String token = jwtUtil.generateToken(userDetails.getUsername(), 3600000L);
		assertNotNull(token);
		assertTrue(token.length() > 0);
	}

	@DisplayName("Test Extract Username from Token")
	@Test
	void testExtractUsername() {
		// Adding the expiration time here too
		String token = jwtUtil.generateToken("swasti@example.com", 3600000L);
		String extractedUsername = jwtUtil.extractUsername(token);
		assertEquals("swasti@example.com", extractedUsername);
	}

	@DisplayName("Test Token Validation - Success")
	@Test
	void testValidateToken_Success() {
		// Generate the token
		String token = jwtUtil.generateToken(userDetails.getUsername(), 3600000L);

		// Remove userDetails from the call to match your method signature
		Boolean isValid = jwtUtil.validateToken(token);

		assertTrue(isValid);
	}
}