package org.example.controller;

import org.example.config.SecurityConfig;
import org.example.dto.UserRegistrationDto;
import org.example.service.CustomUserDetailsService;
import org.example.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;

	@MockBean
	private CustomUserDetailsService customUserDetailsService;

	@Test
	void loginPage_Success() throws Exception {
		mockMvc.perform(get("/login"))
				.andExpect(status().isOk())
				.andExpect(view().name("login"));
	}

	@Test
	void registerPage_Success() throws Exception {
		mockMvc.perform(get("/register"))
				.andExpect(status().isOk())
				.andExpect(view().name("register"))
				.andExpect(model().attributeExists("user"));
	}

	@Test
	void registerUser_Success() throws Exception {
		mockMvc.perform(post("/register")
						.with(SecurityMockMvcRequestPostProcessors.csrf())
						.param("username", "testuser")
						.param("password", "password"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login?registered"));

		verify(userService).createUser("testuser", "password");
	}

	@Test
	void registerUser_Error() throws Exception {
		doThrow(new RuntimeException("Username taken"))
				.when(userService).createUser(any(), any());

		mockMvc.perform(post("/register")
						.with(SecurityMockMvcRequestPostProcessors.csrf())
						.param("username", "testuser")
						.param("password", "password"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/register?error"));

		verify(userService).createUser("testuser", "password");
	}
}