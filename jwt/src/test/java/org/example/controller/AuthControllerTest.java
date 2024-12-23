package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.config.JwtRequestFilter;
import org.example.dto.DTOs.*;
import org.example.service.AuthService;
import org.example.utils.JwtTokenUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(value = AuthController.class,
		excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
				classes = JwtRequestFilter.class))
class AuthControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AuthService authService;

	@MockBean
	private JwtTokenUtils jwtTokenUtils;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@WithMockUser
	void whenValidInput_thenReturnsToken() throws Exception {
		// Arrange
		JwtRequest loginRequest = new JwtRequest();
		loginRequest.setUsername("test");
		loginRequest.setPassword("password");

		JwtResponse jwtResponse = new JwtResponse("token");
		when(authService.createAuthToken(any(JwtRequest.class)))
				.thenReturn(ResponseEntity.ok(jwtResponse));

		// Act & Assert
		mockMvc.perform(post("/api/v1/auth/login")
						.with(SecurityMockMvcRequestPostProcessors.csrf())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(loginRequest)))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").value("token"));
	}

	@Test
	@WithMockUser
	void whenValidRegistration_thenReturnsUser() throws Exception {
		// Arrange
		RegistrationUserDto registrationDto = new RegistrationUserDto();
		registrationDto.setUsername("newuser");
		registrationDto.setPassword("password");
		registrationDto.setConfirmPassword("password");
		registrationDto.setEmail("test@example.com");

		UserDto responseUser = new UserDto(1L, "newuser", "test@example.com");
		when(authService.createNewUser(any(RegistrationUserDto.class)))
				.thenReturn(ResponseEntity.ok(responseUser));

		// Act & Assert
		mockMvc.perform(post("/api/v1/auth/register")
						.with(SecurityMockMvcRequestPostProcessors.csrf())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(registrationDto)))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.username").value("newuser"))
				.andExpect(jsonPath("$.email").value("test@example.com"));
	}
}