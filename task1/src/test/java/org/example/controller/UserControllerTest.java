package org.example.controller;

import org.example.entity.User;
import org.example.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@WebMvcTest(UserController.class)
class UserControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;

	@Test
	@WithMockUser(username = "user", roles = {"USER"})
	void whenGetAllUsers_thenReturnJsonArray() throws Exception {
		// Подготавливаем тестовые данные
		User user = User.builder()
				.id(1L)
				.name("Test User")
				.email("test@example.com")
				.build();

		List<User> allUsers = Collections.singletonList(user);

		// Определяем поведение мока
		given(userService.getAllUsers()).willReturn(allUsers);

		// Выполняем тест
		mockMvc.perform(get("/api/users")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].id", is(1)))
				.andExpect(jsonPath("$[0].name", is("Test User")))
				.andExpect(jsonPath("$[0].email", is("test@example.com")))
				.andExpect(jsonPath("$[0].orders").doesNotExist());
	}

	@Test
	@WithMockUser(username = "user", roles = {"USER"})
	void whenGetUserById_thenReturnUser() throws Exception {
		User user = User.builder()
				.id(1L)
				.name("Test User")
				.email("test@example.com")
				.orders(new ArrayList<>())
				.build();

		given(userService.getUserById(1L)).willReturn(user);

		mockMvc.perform(get("/api/users/1")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.name", is("Test User")))
				.andExpect(jsonPath("$.email", is("test@example.com")))
				.andExpect(jsonPath("$.orders").exists());
	}
}