package org.example.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dtos.JwtRequest;
import org.example.dtos.JwtResponse;
import org.example.dtos.RegistrationUserDto;
import org.example.dtos.UserDto;
import org.example.services.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AuthService authService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void authenticate_Success() throws Exception {
		JwtRequest request = new JwtRequest("user", "password");
		JwtResponse response = new JwtResponse("test-token");

		when(authService.createAuthToken(any(JwtRequest.class)))
				.thenReturn(response);

		mockMvc.perform(post("/api/v1/auth")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(content().json("{\"token\":\"test-token\"}"));
	}

	@Test
	void authenticate_Failure() throws Exception {
		JwtRequest request = new JwtRequest("user", "wrong-password");

		when(authService.createAuthToken(any(JwtRequest.class)))
				.thenThrow(new BadCredentialsException("Incorrect username or password"));

		mockMvc.perform(post("/api/v1/auth")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void register_Success() throws Exception {
		RegistrationUserDto request = new RegistrationUserDto("user", "password", "password", "user@test.com");
		UserDto response = new UserDto(1L, "user", "user@test.com", true);

		when(authService.createNewUser(any(RegistrationUserDto.class)))
				.thenReturn(response);

		mockMvc.perform(post("/api/v1/registration")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(response)));
	}

	@Test
	void register_Failure() throws Exception {
		RegistrationUserDto request = new RegistrationUserDto("user", "password", "different", "user@test.com");

		when(authService.createNewUser(any(RegistrationUserDto.class)))
				.thenThrow(new RuntimeException("Passwords don't match"));

		mockMvc.perform(post("/api/v1/registration")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest());
	}
}