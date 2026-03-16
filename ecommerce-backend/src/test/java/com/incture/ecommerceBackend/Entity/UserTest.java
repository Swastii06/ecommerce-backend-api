package com.incture.ecommerceBackend.Entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

public class UserTest {

	@Test
	public void testGettersAndSetters() {
		User user = new User();
		user.setId(1L);
		user.setName("Swastideepa");
		user.setEmail("swasti@gmail.com");
		user.setPassword("securepass");
		user.setRole("ADMIN");

		assertEquals(1L, user.getId());
		assertEquals("Swastideepa", user.getName());
		assertEquals("swasti@gmail.com", user.getEmail());
		assertEquals("securepass", user.getPassword());
		assertEquals("ADMIN", user.getRole());
	}

	@Test
	public void testAllArgsConstructor() {
		User user = new User("Tejesh", "tejesh@gmail.com", "1234", "CUSTOMER");

		assertEquals("Tejesh", user.getName());
		assertEquals("tejesh@gmail.com", user.getEmail());
		assertEquals("1234", user.getPassword());
		assertEquals("CUSTOMER", user.getRole());
	}

	@Test
	public void testUserDetailsMethods() {
		User user = new User();
		user.setEmail("test@test.com");
		user.setRole("CUSTOMER");

		// Test the boolean methods
		assertTrue(user.isAccountNonExpired());
		assertTrue(user.isAccountNonLocked());
		assertTrue(user.isCredentialsNonExpired());
		assertTrue(user.isEnabled());

		// Test getUsername (which returns email)
		assertEquals("test@test.com", user.getUsername());

		// Test getAuthorities
		Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
		assertNotNull(authorities);
		assertEquals(1, authorities.size());
		assertEquals("CUSTOMER", authorities.iterator().next().getAuthority());
	}
}