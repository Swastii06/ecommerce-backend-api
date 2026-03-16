package com.incture.ecommerceBackend.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.incture.ecommerceBackend.Entity.User;
import com.incture.ecommerceBackend.Repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private UserService userService;

	private User testUser;

	@BeforeEach
	void setUp() {
		testUser = new User();
		testUser.setId(1L);
		testUser.setName("Swasti");
		testUser.setEmail("swasti@example.com");
		testUser.setPassword("oldPassword123");
		testUser.setRole("CUSTOMER");
	}

	@DisplayName("Test Load User By Username (Spring Security)")
	@Test
	void testLoadUserByUsername_Success() {
		when(userRepository.findByEmail("swasti@example.com")).thenReturn(Optional.of(testUser));

		UserDetails userDetails = userService.loadUserByUsername("swasti@example.com");

		assertNotNull(userDetails);
		assertEquals("swasti@example.com", userDetails.getUsername());
	}

	@DisplayName("Test Load User By Username - Not Found")
	@Test
	void testLoadUserByUsername_NotFound() {
		when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

		assertThrows(UsernameNotFoundException.class, () -> {
			userService.loadUserByUsername("unknown@example.com");
		});
	}

	@DisplayName("Test Get User By Email - Success")
	@Test
	void testGetUserByEmail_Success() {
		when(userRepository.findByEmail("swasti@example.com")).thenReturn(Optional.of(testUser));

		User result = userService.getUserByEmail("swasti@example.com");

		assertNotNull(result);
		assertEquals("Swasti", result.getName());
	}

	@DisplayName("Test Update Profile")
	@Test
	void testUpdateProfile_Success() {
		User updatedInfo = new User();
		updatedInfo.setName("Swastideepa");

		when(userRepository.findByEmail("swasti@example.com")).thenReturn(Optional.of(testUser));
		when(userRepository.save(any(User.class))).thenReturn(testUser);

		User result = userService.updateProfile("swasti@example.com", updatedInfo);

		assertEquals("Swastideepa", result.getName());
		verify(userRepository, times(1)).save(any(User.class));
	}

	@DisplayName("Test Change Password")
	@Test
	void testChangePassword() {
		String newPlainPassword = "newSecurePassword789";
		String encodedPassword = "encoded_newSecurePassword789";

		when(userRepository.findByEmail("swasti@example.com")).thenReturn(Optional.of(testUser));
		when(passwordEncoder.encode(newPlainPassword)).thenReturn(encodedPassword);

		userService.changePassword("swasti@example.com", newPlainPassword, passwordEncoder);

		assertEquals(encodedPassword, testUser.getPassword());
		verify(userRepository, times(1)).save(testUser);
	}

	@DisplayName("Test Update User By Admin")
	@Test
	void testUpdateUserByAdmin() {
		User adminUpdates = new User();
		adminUpdates.setName("Admin Updated Name");
		adminUpdates.setEmail("admin_updated@example.com");
		adminUpdates.setRole("ADMIN");

		when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
		when(userRepository.save(any(User.class))).thenReturn(testUser);

		User result = userService.updateUserByAdmin(1L, adminUpdates);

		assertEquals("Admin Updated Name", result.getName());
		assertEquals("ADMIN", result.getRole());
		verify(userRepository, times(1)).save(any(User.class));
	}

	@DisplayName("Test Delete User")
	@Test
	void testDeleteUser() {
		userService.deleteUser(1L);
		verify(userRepository, times(1)).deleteById(1L);
	}
}