package com.incture.ecommerceBackend.Config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.incture.ecommerceBackend.Filter.JwtAuthenticationFilter;
import com.incture.ecommerceBackend.Utils.JwtUtil;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private JwtUtil jwtUtil;
	private UserDetailsService userDetailsService;

	@Autowired
	public SecurityConfig(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
		this.jwtUtil = jwtUtil;
		this.userDetailsService = userDetailsService;
	}

	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
		// Creates a bean for BCrypt password encryption and is used for storing
		// passwords & verifying passwords during login.
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager)
			throws Exception { // This method configures how HTTP requests are secured

		JwtAuthenticationFilter jwtAuthFilter = new JwtAuthenticationFilter(authenticationManager, jwtUtil,
				userDetailsService);
		// Creates a custom filter that extracts JWT token, validates token &
		// authenticates user

		http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(auth -> auth

				// PUBLIC ENDPOINTS
				.requestMatchers("/api/users/register", "/api/users/login", "/error", "/v3/api-docs/**",
						"/swagger-ui/**", "/swagger-ui.html")
				.permitAll()
				// Allow anyone to view products
				.requestMatchers(org.springframework.http.HttpMethod.GET, "/api/products", "/api/products/**")
				.permitAll()

				// ADMIN-ONLY ENDPOINTS (Requires "ADMIN" authority)
				.requestMatchers(org.springframework.http.HttpMethod.GET, "/api/users/**")
				.hasAnyAuthority("ADMIN", "ROLE_ADMIN")
				.requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/users/**")
				.hasAnyAuthority("ADMIN", "ROLE_ADMIN")
				.requestMatchers(org.springframework.http.HttpMethod.POST, "/api/products/**")
				.hasAnyAuthority("ADMIN", "ROLE_ADMIN")
				.requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/products/**")
				.hasAnyAuthority("ADMIN", "ROLE_ADMIN")
				.requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/products/**")
				.hasAnyAuthority("ADMIN", "ROLE_ADMIN")

				// SECURED CUSTOMER ENDPOINTS (Cart, Checkout, Profile)
				.anyRequest().authenticated())

				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager() {
		return new ProviderManager(Arrays.asList(daoAuthenticationProvider()));
		// registers the DaoAuthenticationProvider as the authentication provider
	}
}