package com.incture.ecommerceBackend.Controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.incture.ecommerceBackend.Entity.User;
import com.incture.ecommerceBackend.Service.UserService;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private UserService userService;

	@MockitoBean
	private PasswordEncoder passwordEncoder;

	private ObjectMapper objectMapper;
	private User testUser;

	@BeforeEach
	void setUp() {
		objectMapper = new ObjectMapper();
		// it tells Jackson: If you don't know how to handle a field (like authorities),
		// just skip it
		objectMapper.configure(com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

		testUser = new User();
		testUser.setEmail("swasti@example.com");
		testUser.setName("Swasti");
		testUser.setPassword("rawPassword");
		testUser.setRole("CUSTOMER");
	}

	@DisplayName("POST /api/users/register - Success")
	@Test
	void testRegister_Success() throws Exception {
		when(passwordEncoder.encode(anyString())).thenReturn("encryptedPassword");

		mockMvc.perform(post("/api/users/register").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(testUser))).andExpect(status().isOk())
				.andExpect(content().string("User registered successfully"));

		verify(passwordEncoder, times(1)).encode("rawPassword");
		verify(userService, times(1)).save(any(User.class));
	}

	@DisplayName("PUT /api/users/profile - Success")
	@Test
	void testUpdateProfile() throws Exception {
		when(userService.updateProfile(anyString(), any(User.class))).thenReturn(testUser);

		mockMvc.perform(put("/api/users/profile").principal(() -> "swasti@example.com")
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(testUser)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Swasti"));
	}

	@DisplayName("PUT /api/users/change-password - Success")
	@Test
	void testChangePassword() throws Exception {
		mockMvc.perform(put("/api/users/change-password").param("newPassword", "newSecurePass")
				.principal(() -> "swasti@example.com")).andExpect(status().isOk())
				.andExpect(content().string("Password changed successfully!"));

		verify(userService, times(1)).changePassword(anyString(), anyString(), any());
	}

	@DisplayName("GET /api/users/{id} - Admin Flow")
	@Test
	void testGetUserById() throws Exception {
		testUser.setId(1L);
		when(userService.getUserById(1L)).thenReturn(testUser);

		mockMvc.perform(get("/api/users/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.email").value("swasti@example.com"));
	}

	@DisplayName("DELETE /api/users/{id} - Admin Flow")
	@Test
	void testDeleteUser() throws Exception {
		mockMvc.perform(delete("/api/users/1")).andExpect(status().isOk())
				.andExpect(content().string("User deleted successfully!"));

		verify(userService, times(1)).deleteUser(1L);
	}
}