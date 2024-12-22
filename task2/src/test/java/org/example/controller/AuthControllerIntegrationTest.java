package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.AuthenticationRequest;
import org.example.dto.RegisterRequest;
import org.example.model.Role;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private UserRepository userRepository;

	private RegisterRequest registerRequest;
	private AuthenticationRequest authRequest;

	@BeforeEach
	void setUp() {
		// Clean up the database before each test
		userRepository.deleteAll();

		// Initialize test data
		registerRequest = new RegisterRequest();
		registerRequest.setUsername("testuser");
		registerRequest.setPassword("password123");

		authRequest = new AuthenticationRequest();
		authRequest.setUsername("testuser");
		authRequest.setPassword("password123");
	}

	@Test
	void register_SuccessfulRegistration() throws Exception {
		// Act & Assert
		MvcResult result = mockMvc.perform(post("/api/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(registerRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.accessToken").exists())
				.andExpect(jsonPath("$.refreshToken").exists())
				.andReturn();

		// Verify user was created in database
		User user = userRepository.findByUsername("testuser").orElseThrow();
		assertEquals(Role.USER, user.getRole());
		assertTrue(user.isAccountNonLocked());
	}

	@Test
	void register_DuplicateUsername() throws Exception {
		// Arrange
		mockMvc.perform(post("/api/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(registerRequest)))
				.andExpect(status().isOk());

		// Act & Assert
		mockMvc.perform(post("/api/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(registerRequest)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void authenticate_SuccessfulLogin() throws Exception {
		// Arrange
		mockMvc.perform(post("/api/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(registerRequest)))
				.andExpect(status().isOk());

		// Act & Assert
		mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(authRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.accessToken").exists())
				.andExpect(jsonPath("$.refreshToken").exists());
	}

	@Test
	void authenticate_AccountLockAfterFailedAttempts() throws Exception {
		// First register a new user
		mockMvc.perform(post("/api/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(registerRequest)))
				.andExpect(status().isOk());

		// Create an authentication request with wrong password
		AuthenticationRequest wrongPasswordRequest = new AuthenticationRequest();
		wrongPasswordRequest.setUsername("testuser");
		wrongPasswordRequest.setPassword("wrongpassword");

		// Attempt to login with wrong password 5 times
		for (int i = 0; i < 5; i++) {
			mockMvc.perform(post("/api/auth/login")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(wrongPasswordRequest)))
					.andExpect(status().isUnauthorized());
		}

		// Verify that even with correct password, account is now locked
		mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(authRequest)))
				.andExpect(status().isUnauthorized());

		// Verify account lock status in database
		User user = userRepository.findByUsername("testuser").orElseThrow();
		assertFalse(user.isAccountNonLocked());
		assertEquals(5, user.getFailedAttempts());
		assertNotNull(user.getLockTime());
	}

	@Test
	void refreshToken_ValidRefreshToken() throws Exception {
		// First register and get initial tokens
		MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(registerRequest)))
				.andExpect(status().isOk())
				.andReturn();

		String response = registerResult.getResponse().getContentAsString();
		String refreshToken = objectMapper.readTree(response).get("refreshToken").asText();

		// Try to get new access token using refresh token
		mockMvc.perform(post("/api/auth/refresh-token")
						.header("Authorization", "Bearer " + refreshToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.accessToken").exists())
				.andExpect(jsonPath("$.refreshToken").exists());
	}

	@Test
	void refreshToken_InvalidRefreshToken() throws Exception {
		// Try to refresh with invalid token
		String invalidToken = "invalid.refresh.token";

		mockMvc.perform(post("/api/auth/refresh-token")
						.header("Authorization", "Bearer " + invalidToken))
				.andExpect(status().isUnauthorized());
	}
}