package com.incture.ecommerceBackend.Filter;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.incture.ecommerceBackend.DTO.LoginRequest;
import com.incture.ecommerceBackend.Utils.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;
	private final UserDetailsService userDetailsService;

	public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
			UserDetailsService userDetailsService) {
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		// Handle Login
		if (request.getServletPath().equals("/api/users/login")) {
			ObjectMapper objectMapper = new ObjectMapper();
			LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);

			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
					loginRequest.getEmail(), loginRequest.getPassword());

			Authentication authResult = authenticationManager.authenticate(authToken);

			if (authResult.isAuthenticated()) {
				// Get role from authorities (e.g., ADMIN or USER)
				String role = authResult.getAuthorities().stream().map(r -> r.getAuthority()).findFirst()
						.orElse("USER");

				String token = jwtUtil.generateToken(authResult.getName(), role);

				response.setHeader("Authorization", "Bearer " + token);
				response.getWriter().write("Login Successful! Token in header.");
			}
			return;
		}

		// Handle Token Validation
		String authHeader = request.getHeader("Authorization");

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);

			if (jwtUtil.validateToken(token)) {
				String email = jwtUtil.extractUsername(token);

				// Extract the role claim specifically
				String role = jwtUtil.extractClaim(token, claims -> claims.get("role", String.class));

				UserDetails userDetails = userDetailsService.loadUserByUsername(email);

				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
						null, Collections.singletonList(new SimpleGrantedAuthority(role)));

				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}
		}

		filterChain.doFilter(request, response);
	}
}