package com.incture.ecommerceBackend.Filter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.incture.ecommerceBackend.Utils.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
public class JwtFilterTest {

	@Mock
	private JwtUtil jwtUtil;

	@Mock
	private UserDetailsService userDetailsService;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	private FilterChain filterChain;

	@InjectMocks
	private JwtAuthenticationFilter jwtFilter;

	@Test
	void testFilterExecution_WithValidToken() throws Exception {
		// 1. Mock the Authorization Header
		when(request.getHeader("Authorization")).thenReturn("Bearer mock-token");
		when(request.getServletPath()).thenReturn("/api/products/1");

		// 2. Mock Token Validation
		when(jwtUtil.validateToken("mock-token")).thenReturn(true);
		when(jwtUtil.extractUsername("mock-token")).thenReturn("swasti@example.com");

		// FIX: Mock the role extraction so it returns a real authority string
		when(jwtUtil.extractClaim(anyString(), any())).thenReturn("ADMIN");

		// 3. Mock User Loading
		UserDetails userDetails = new User("swasti@example.com", "password", Collections.emptyList());
		when(userDetailsService.loadUserByUsername("swasti@example.com")).thenReturn(userDetails);

		// 4. Execute Filter
		jwtFilter.doFilterInternal(request, response, filterChain);

		// 5. Verify the chain continues
		verify(filterChain).doFilter(request, response);
	}
}