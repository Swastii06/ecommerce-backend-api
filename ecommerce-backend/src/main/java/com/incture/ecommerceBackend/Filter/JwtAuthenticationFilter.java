package com.incture.ecommerceBackend.Filter;

import java.io.IOException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
			throws ServletException, IOException { // This method runs for every incoming HTTP request, it decides
													// whether to generate a token or validate an existing token

		// If login request: Generate the Token
		if (request.getServletPath().equals("/api/users/login")) {
			ObjectMapper objectMapper = new ObjectMapper(); // converts JSON request body into Java object
			LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
			// Reads the request body and converts it into LoginRequest object which
			// contains email and password.

			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
					loginRequest.getEmail(), loginRequest.getPassword()); // Creates an authentication token using email
																			// and password which verifies the
																			// credentials using UserDetailsService
			Authentication authResult = authenticationManager.authenticate(authToken);

			if (authResult.isAuthenticated()) { // If authentication succeeds, generate a JWT token

				String token = jwtUtil.generateToken(authResult.getName(), 60); // Generates a JWT token containing the
																				// username & token expiry is set to 60
																				// minutes
				response.setHeader("Authorization", "Bearer " + token); // Sends the generated JWT token back in the
																		// response header
				response.getWriter().write("Login Successful! Token in header.");
			}
			return; // Stops further processing since login is already handled
		}

		// For all other tokens: Read and Validate the Token
		String authHeader = request.getHeader("Authorization"); // Reads the Authorization header from the request

		if (authHeader != null && authHeader.startsWith("Bearer ")) { // Checks whether the Authorization header
																		// contains a Bearer token
			String token = authHeader.substring(7); // Remove "Bearer " prefix

			if (jwtUtil.validateToken(token)) { // Validates the token using JwtUtil, Checks token signature, format,
												// and expiry.
				String email = jwtUtil.extractUsername(token);
				UserDetails userDetails = userDetailsService.loadUserByUsername(email); // Loads the user details from
																						// the database using the
																						// extracted email

				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
						null, userDetails.getAuthorities()); // Creates an authenticated token containing user details
																// and roles
				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext().setAuthentication(authToken); // Stores the authentication
																					// information in Spring Security
																					// context (tells Spring that the
																					// user is authenticated for this
																					// request)
			}
		}

		filterChain.doFilter(request, response); // Passes the request to the next filter in the chain, If
													// authentication is successful, the request proceeds to the
													// controller.
	}
}